package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.AuthResponse;
import com.hostelmanagersystem.dto.response.IntrospectResponse;
import com.hostelmanagersystem.entity.identity.InvalidatedToken;
import com.hostelmanagersystem.entity.identity.ResetToken;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.repository.InvalidatedTokenRepository;
import com.hostelmanagersystem.repository.ResetTokenRepository;
import com.hostelmanagersystem.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository tokenRepository;
    JavaMailSender mailSender;
    ResetTokenRepository resetTokenRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGN_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthResponse authenticate(AuthRequest authRequest){
        var user = userRepository.findByUserName(authRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(user.getIsActive() == false){
            throw new AppException(ErrorCode.USER_HAD_BANNED);
        }
        var token = generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .userName(user.getUserName())
                .build();
    }
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // thuật toán mã hóa

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId()) // set subject bang id cua user
                .issuer("Quan ly phong tro 22DTHD5")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now()
                                .plus(VALID_DURATION, ChronoUnit.SECONDS)
                                .toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scopeBuider(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
            throw new RuntimeException(e);
        }
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException{
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token,false);
        }catch (AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationDate = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!verified && expirationDate.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (tokenRepository.existsById((signedJWT.getJWTClaimsSet().getJWTID()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }
    public AuthResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        tokenRepository.save(invalidatedToken);
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findById(username).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var token = generateToken(user);

        return AuthResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
            tokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.error("Token already expired");
        }
    }
    private String scopeBuider(User user) {
        StringJoiner scope = new StringJoiner("");
        var userRoles = user.getRole();
        if (userRoles != null) {
            scope.add("ROLE_" + userRoles.getName());
        }
        return scope.toString();
    }
    public String createNewPassword(String token, String newPassword) {
        var resetToken = resetTokenRepository.findById(token);
        if(resetToken.isEmpty()){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var tokenEntity = resetToken.get();
        if(tokenEntity.getExpiryDate().isBefore(Instant.now())){
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        var user = userRepository.findByEmail(tokenEntity.getEmail());
        if(user.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        var userEntity = user.get();

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        resetTokenRepository.delete(tokenEntity);
        return "Mật khẩu đã được đặt lại thành công.";
    }
    public String processForgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail());
        if(user.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

       var resetToken = ResetToken.builder()
                       .token(UUID.randomUUID().toString())
                          .email(request.getEmail())
                            .expiryDate(Instant.now().plus(5, ChronoUnit.MINUTES))
                          .build();
        resetTokenRepository.save(resetToken);
        sendResetEmail(request.getEmail(),resetToken.getToken());
        return "Hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn.";
    }

    private void sendResetEmail(String email, String token) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu");
            helper.setText(
                    "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                            "<div style=\"text-align: center; padding-bottom: 15px; border-bottom: 2px solid #f2f2f2;\">" +
                            "<h2 style=\"color: #3a3a3a; margin-bottom: 5px;\">Yêu Cầu Đặt Lại Mật Khẩu</h2>" +
                            "<p style=\"color: #777777; font-size: 14px;\">Hãy thực hiện bước tiếp theo để bảo vệ tài khoản của bạn</p>" +
                            "</div>" +

                            "<div style=\"padding: 20px 0;\">" +
                            "<p style=\"color: #444; line-height: 1.5;\">Xin chào,</p>" +
                            "<p style=\"color: #444; line-height: 1.5;\">Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Liên kết dưới đây sẽ giúp bạn tạo mật khẩu mới.</p>" +

                            "<div style=\"text-align: center; margin: 30px 0;\">" +
                            "<a href=\"http://localhost:3000/reset-password?token=" + token + "\" style=\"background-color: #4361ee; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; box-shadow: 0 2px 5px rgba(67, 97, 238, 0.3);\">Đặt Lại Mật Khẩu</a>" +
                            "</div>" +

                            "<p style=\"color: #444; line-height: 1.5;\">Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ đội hỗ trợ của chúng tôi.</p>" +

                            "<div style=\"background-color: #f9f9f9; padding: 12px; border-radius: 6px; margin-top: 20px; border-left: 4px solid #ffd166;\">" +
                            "<p style=\"color: #666; margin: 0; font-size: 14px;\"><strong>Lưu ý quan trọng:</strong> Liên kết này chỉ có hiệu lực trong vòng <strong>5 phút</strong> kể từ khi email được gửi.</p>" +
                            "</div>" +
                            "</div>" +

                            "<div style=\"text-align: center; padding-top: 15px; border-top: 2px solid #f2f2f2; font-size: 14px; color: #888;\">" +
                            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                            "<p style=\"margin-top: 5px;\">© " + new Date().getYear() + " 22DTHD5`- Mọi quyền được bảo lưu</p>" +
                            "</div>" +
                            "</div>",
                    true
            );
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            log.error("Error sending email: {}", e.getMessage());
        }
    }
}
