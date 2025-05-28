package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.InvoiceMapper;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.mapper.UserMapper;
import com.hostelmanagersystem.repository.InvoiceRepository;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.UserRepository;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AdminService {
    UserRepository userRepository;
    UserMapper userMapper;
    JavaMailSender mailSender;
    RoomRepository roomRepository;
    RoomMapper roomMapper;
    InvoiceRepository invoiceRepository;
    InvoiceMapper invoiceMapper;


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUserById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
        return "Đã xoá người dùng";
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String banUserById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(false);
        sendAccountLockedEmail(user.getEmail());
        userRepository.save(user);
        return "Đã khoá tài khoản người dùng";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String unbanUserById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(true);
        sendAccountUnlockedEmail(user.getEmail());
        userRepository.save(user);
        return "Đã mở khoá tài khoản người dùng";
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String acceptRoomCreateRequest(String id){
        Room roomRequest = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        if(roomRequest.getStatus() == RoomStatus.PENDING){
            roomRequest.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(roomRequest);
        }
        return "Đã chấp nhận yêu cầu";
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRoomCreateRequest(String id){
        Room roomRequest = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        if(roomRequest.getStatus() != RoomStatus.PENDING){
            throw new AppException(ErrorCode.ROOM_HAD_BEEN_ACCEPT);
        }
        roomRepository.delete(roomRequest);
        return "Đã từ chối yêu cầu";
    }
    // Email thông báo tài khoản bị khóa
    private void sendAccountLockedEmail(String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Thông báo tài khoản bị khóa");
            helper.setText(
                    "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                            "<div style=\"text-align: center; padding-bottom: 15px; border-bottom: 2px solid #f2f2f2;\">" +
                            "<h2 style=\"color: #3a3a3a; margin-bottom: 5px;\">Thông Báo Tài Khoản Bị Khóa</h2>" +
                            "<p style=\"color: #777777; font-size: 14px;\">Thông tin quan trọng về tài khoản của bạn</p>" +
                            "</div>" +

                            "<div style=\"padding: 20px 0;\">" +
                            "<p style=\"color: #444; line-height: 1.5;\">Xin chào,</p>" +
                            "<p style=\"color: #444; line-height: 1.5;\">Chúng tôi thông báo rằng tài khoản của bạn đã bị tạm khóa vì lý do sau:</p>" +

                            "<div style=\"background-color: #fff1f0; padding: 12px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #ff4d4f;\">" +
                            "<p style=\"color: #cf1322; margin: 0; font-size: 15px;\"><strong>Lý do khóa tài khoản:</strong> " + "vi phạm quy tắc trang web" + "</p>" +
                            "</div>" +

                            "<p style=\"color: #444; line-height: 1.5;\">Để mở khóa tài khoản, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi qua email phamvansy204@gmail.com hoặc số điện thoại 1900-xxxx.</p>" +

                            "<div style=\"text-align: center; margin: 30px 0;\">" +
                            "<a href=\"http://localhost:3000/contact\" style=\"background-color: #ff7875; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; box-shadow: 0 2px 5px rgba(255, 77, 79, 0.3);\">Liên Hệ Hỗ Trợ</a>" +
                            "</div>" +

                            "<div style=\"background-color: #f9f9f9; padding: 12px; border-radius: 6px; margin-top: 20px; border-left: 4px solid #ffd166;\">" +
                            "<p style=\"color: #666; margin: 0; font-size: 14px;\"><strong>Lưu ý:</strong> Nếu bạn cho rằng đây là sự nhầm lẫn, vui lòng phản hồi email này hoặc liên hệ trực tiếp với chúng tôi càng sớm càng tốt.</p>" +
                            "</div>" +
                            "</div>" +

                            "<div style=\"text-align: center; padding-top: 15px; border-top: 2px solid #f2f2f2; font-size: 14px; color: #888;\">" +
                            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                            "<p style=\"margin-top: 5px;\">© " + new Date().getYear() + " 22DTHD5 - Mọi quyền được bảo lưu</p>" +
                            "</div>" +
                            "</div>",
                    true
            );
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error sending account locked email: {}", e.getMessage());
        }
    }

    // Email thông báo tài khoản được mở khóa
    private void sendAccountUnlockedEmail(String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Thông báo tài khoản đã được mở khóa");
            helper.setText(
                    "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                            "<div style=\"text-align: center; padding-bottom: 15px; border-bottom: 2px solid #f2f2f2;\">" +
                            "<h2 style=\"color: #3a3a3a; margin-bottom: 5px;\">Tài Khoản Đã Được Mở Khóa</h2>" +
                            "<p style=\"color: #777777; font-size: 14px;\">Chào mừng bạn trở lại!</p>" +
                            "</div>" +

                            "<div style=\"padding: 20px 0;\">" +
                            "<p style=\"color: #444; line-height: 1.5;\">Xin chào,</p>" +
                            "<p style=\"color: #444; line-height: 1.5;\">Chúng tôi vui mừng thông báo rằng tài khoản của bạn đã được mở khóa thành công và hiện đã hoạt động bình thường.</p>" +

                            "<div style=\"background-color: #f6ffed; padding: 12px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #52c41a;\">" +
                            "<p style=\"color: #389e0d; margin: 0; font-size: 15px;\"><strong>Trạng thái tài khoản:</strong> Đã mở khóa và hoạt động</p>" +
                            "</div>" +

                            "<p style=\"color: #444; line-height: 1.5;\">Bây giờ bạn có thể đăng nhập và sử dụng tất cả các dịch vụ của chúng tôi như bình thường.</p>" +

                            "<div style=\"text-align: center; margin: 30px 0;\">" +
                            "<a href=\"http://localhost:3000/login\" style=\"background-color: #52c41a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; box-shadow: 0 2px 5px rgba(82, 196, 26, 0.3);\">Đăng Nhập Ngay</a>" +
                            "</div>" +

                            "<p style=\"color: #444; line-height: 1.5;\">Chúng tôi đánh giá cao sự kiên nhẫn và hợp tác của bạn trong việc giải quyết vấn đề này.</p>" +

                            "<div style=\"background-color: #f9f9f9; padding: 12px; border-radius: 6px; margin-top: 20px; border-left: 4px solid #1890ff;\">" +
                            "<p style=\"color: #666; margin: 0; font-size: 14px;\"><strong>Lưu ý:</strong> Nếu bạn gặp bất kỳ vấn đề nào khi đăng nhập, vui lòng liên hệ với đội hỗ trợ của chúng tôi.</p>" +
                            "</div>" +
                            "</div>" +

                            "<div style=\"text-align: center; padding-top: 15px; border-top: 2px solid #f2f2f2; font-size: 14px; color: #888;\">" +
                            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                            "<p style=\"margin-top: 5px;\">© " + new Date().getYear() + " 22DTHD5 - Mọi quyền được bảo lưu</p>" +
                            "</div>" +
                            "</div>",
                    true
            );
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error sending account unlocked email: {}", e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoomResponse> getAllPendingRoomRequests(){
        return roomRepository.findAllByStatus(RoomStatus.PENDING)
                .stream().map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<InvoiceResponse> getAllInvoice(){
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUserByFirstNameContaining(String firstName){
        List<User> list = userRepository.findAllByFirstNameContainingIgnoreCase(firstName)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        return list.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoomResponse> getAllRoomByRoomNumberContaining(String roomNumber){
        List<Room> list = roomRepository.findAllByRoomNumberContainingIgnoreCase(roomNumber)
                .orElseThrow(() ->new AppException(ErrorCode.ROOM_NOT_EXISTED));
        return list.stream().map(roomMapper::toRoomResponse).collect(Collectors.toList());
    }
}
