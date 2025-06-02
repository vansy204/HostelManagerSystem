package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.manager.UserActivityLog;
import com.hostelmanagersystem.repository.UserActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityLoggerService {
    UserActivityLogRepository userActivityLogRepository;
    HttpServletRequest httpServletRequest;

    @Async // su dung async de khong block main thread
    public void logUserActivity(String userId,String action,String description) {
        logUserActivity(userId,action,description,null,null);
    }
    @Async
    public void logUserActivity(String userId, String action, String description,
                                Map<String, Object> requestData,Map<String,Object> responseData) {
        try{
            UserActivityLog userActivityLog = UserActivityLog.builder()
                    .userId(userId)
                    .userName(getCurrentUsername())
                    .action(action)
                    .description(description)
                    .ipAddress(getClientIpAddress())
                    .userAgent(httpServletRequest.getHeader("User-Agent"))
                    .requestUrl(httpServletRequest.getRequestURL().toString())
                    .requestData(requestData)
                    .responseData(responseData)
                    .sessionId(httpServletRequest.getSession().getId())
                    .deviceInfo(parseDeviceInfo())
                    .role(extractRoleFromUrl())
                    .timestamp(LocalDateTime.now())
                    .build();
            userActivityLogRepository.save(userActivityLog);
        }catch (Exception e){
            log.error("Failed to save user activity log",e);
        }
    }
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "guest";
    }
    private String getClientIpAddress() {
        String xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
        if(xForwardedFor != null && !xForwardedFor.isEmpty()){
            return xForwardedFor.split(",")[0].trim();
        }
        String realIp = httpServletRequest.getHeader("X-Real-IP");
        if(realIp != null && !realIp.isEmpty()){
            return realIp;
        }
        return httpServletRequest.getRemoteAddr();
    }
    private String extractRoleFromUrl(){
        String url=httpServletRequest.getRequestURL().toString();
        if(url.contains("/v1/admin")) return "ADMIN";
        if(url.contains("/v1/owner/")) return "OWNER";
        if(url.contains("/v1/tenant")) return "TENANT";
        return "GUEST";
    }
    private String parseDeviceInfo(){
        String userAgent=httpServletRequest.getHeader("User-Agent");
        if(userAgent != null){
            if(userAgent.contains("Mobile"))return "Mobile";
            if(userAgent.contains("Tablet"))return "Tablet";
            return "Desktop";
        }
        return "Unknown";
    }
    public void logWithExecutionTime(String userId,String action,String description, long executionTime) {
        UserActivityLog userActivityLog = UserActivityLog.builder()
                .userId(userId)
                .userName(getCurrentUsername())
                .action(action)
                .description(description)
                .ipAddress(getClientIpAddress())
                .userAgent(httpServletRequest.getHeader("User-Agent"))
                .requestUrl(httpServletRequest.getRequestURL().toString())
                .sessionId(httpServletRequest.getSession().getId())
                .deviceInfo(parseDeviceInfo())
                .role(extractRoleFromUrl())
                .timestamp(LocalDateTime.now())
                .build();
        userActivityLog.setExecutionTime(executionTime);
        userActivityLogRepository.save(userActivityLog);
    }

}
