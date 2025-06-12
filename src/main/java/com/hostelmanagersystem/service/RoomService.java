package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomFilterRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor

public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;
    JavaMailSender mailSender;
    private MongoTemplate mongoTemplate;

    @NonFinal
    @Value("${spring.mail.username}")
    String ADMIN_EMAIL;

    @PreAuthorize("hasRole('OWNER')")
    public String createRoom(@RequestBody RoomCreationRequest room) {
        var existingRoom = roomRepository.findByRoomNumber(room.getRoomNumber());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (existingRoom.isPresent()) {
            throw new AppException(ErrorCode.ROOM_EXISTED);
        }
        Room roomCreate = Room.builder()
                .ownerId(authentication.getName())
                .roomNumber(room.getRoomNumber())
                .roomSize(room.getRoomSize())
                .price(room.getPrice())
                .status(RoomStatus.PENDING)
                .roomType(room.getRoomType())
                .facilities(room.getFacilities())
                .leaseTerm(room.getLeaseTerm())
                .condition(room.getCondition())
                .description(room.getDescription())
                .floor(room.getFloor())
                .mediaUrls(room.getMediaUrls())
                .province(room.getProvince())
                .district(room.getDistrict())
                .ward(room.getWard())
                .addressText(room.getAddressText())
                .build();
        sendRoomApprovalRequestEmail(ADMIN_EMAIL,room);
        roomRepository.save(roomCreate);
        return "Đã gửi yêu cầu đăng bài đến admin, vui lòng chờ được duyệt";
    }

    public Room findRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->new AppException(ErrorCode.ROOM_NOT_EXISTED));
    }
    public Room findRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() ->new AppException(ErrorCode.ROOM_NOT_EXISTED));
    }
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> filterRooms(RoomFilterRequest request) {
        Criteria criteria = new Criteria();

        List<Criteria> criteriaList = new ArrayList<>();


        if (request.getMinPrice() != null) {
            criteriaList.add(Criteria.where("price").gte(request.getMinPrice()));
        }

        if (request.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("price").lte(request.getMaxPrice()));
        }

        if (request.getMinSize() != null) {
            criteriaList.add(Criteria.where("area").gte(request.getMinSize()));
        }
        if (request.getMaxSize() != null) {
            criteriaList.add(Criteria.where("area").lte(request.getMaxSize()));
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getRoomType() != null && !request.getRoomType().isBlank()) {
            criteriaList.add(Criteria.where("roomType").is(request.getRoomType()));
        }

        if (request.getFacilities() != null && !request.getFacilities().isEmpty()) {
            criteriaList.add(Criteria.where("facilities").all(request.getFacilities()));
        }

        if (request.getLeaseTerm() != null) {
            criteriaList.add(Criteria.where("leaseTerm").is(request.getLeaseTerm()));
        }

        if (request.getCondition() != null && !request.getCondition().isBlank()) {
            criteriaList.add(Criteria.where("condition").is(request.getCondition()));
        }

        if (request.getProvince() != null && !request.getProvince().isBlank()) {
            criteriaList.add(Criteria.where("province").is(request.getProvince()));
        }

        if (request.getDistrict() != null && !request.getDistrict().isBlank()) {
            criteriaList.add(Criteria.where("district").is(request.getDistrict()));
        }

        if (request.getWard() != null && !request.getWard().isBlank()) {
            criteriaList.add(Criteria.where("ward").is(request.getWard()));
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            criteriaList.add(Criteria.where("addressText").regex(".*" + request.getKeyword() + ".*", "i"));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Room.class);

    }


    @PreAuthorize("hasRole('OWNER')")
    public Room updateRoom(String roomId, RoomUpdateRequest roomUpdateRequest) {
        Room oldRoom = findRoomById(roomId);
        roomMapper.updateRoom(oldRoom,roomUpdateRequest);
        return roomRepository.save(oldRoom);
    }

    @PreAuthorize("hasRole('OWNER')")
    public String deleteRoom(String roomId) {
        Room oldRoom = findRoomById(roomId);
        roomRepository.delete(oldRoom);
        return "Room deleted successfully";
    }
    private void sendRoomApprovalRequestEmail(String adminEmail, RoomCreationRequest roomRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(adminEmail);
            helper.setSubject("Yêu cầu duyệt bài đăng phòng mới");

            // Định dạng địa chỉ đầy đủ
            String fullAddress = roomRequest.getAddressText() + ", " +
                    roomRequest.getWard() + ", " +
                    roomRequest.getDistrict() + ", " +
                    roomRequest.getProvince();

            // Định dạng tiện ích
            String facilitiesHtml = "";
            if (roomRequest.getFacilities() != null && !roomRequest.getFacilities().isEmpty()) {
                StringBuilder facilitiesBuilder = new StringBuilder("<ul style=\"margin-top: 5px; padding-left: 20px;\">");
                for (String facility : roomRequest.getFacilities()) {
                    facilitiesBuilder.append("<li style=\"margin-bottom: 3px;\">" + facility + "</li>");
                }
                facilitiesBuilder.append("</ul>");
                facilitiesHtml = facilitiesBuilder.toString();
            } else {
                facilitiesHtml = "<p style=\"margin-top: 5px; color: #666;\">Không có</p>";
            }

            // Thông báo về tệp đính kèm
            String attachmentNotice = "";
            if (roomRequest.getMediaUrls() != null && !roomRequest.getMediaUrls().isEmpty()) {
                attachmentNotice = "<div style=\"margin-top: 20px; padding: 15px; background-color: #f0f7ff; border-radius: 8px; border-left: 4px solid #1890ff;\">" +
                        "<h3 style=\"color: #3a3a3a; margin-top: 0; margin-bottom: 10px;\">Hình ảnh và Video</h3>" +
                        "<p style=\"color: #555;\">Email này có kèm theo " + roomRequest.getMediaUrls().size() + " tệp đính kèm (hình ảnh/video). " +
                        "Vui lòng xem các tệp đính kèm để có thông tin trực quan về phòng.</p>" +
                        "</div>";
            }

            helper.setText(
                    "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                            "<div style=\"text-align: center; padding-bottom: 15px; border-bottom: 2px solid #f2f2f2;\">" +
                            "<h2 style=\"color: #3a3a3a; margin-bottom: 5px;\">Yêu Cầu Duyệt Bài Đăng Phòng</h2>" +
                            "<p style=\"color: #777777; font-size: 14px;\">Hệ thống vừa nhận được một yêu cầu đăng bài phòng mới</p>" +
                            "</div>" +

                            "<div style=\"padding: 20px 0;\">" +
                            "<p style=\"color: #444; line-height: 1.5;\">Kính gửi Quản trị viên,</p>" +
                            "<p style=\"color: #444; line-height: 1.5;\">Hệ thống vừa nhận được một yêu cầu đăng bài phòng mới. Dưới đây là thông tin chi tiết về phòng cần được duyệt:</p>" +

                            "<div style=\"background-color: #f8f8f8; padding: 20px; border-radius: 8px; margin: 20px 0;\">" +
                            "<h3 style=\"color: #3a3a3a; margin-top: 0; margin-bottom: 15px; border-bottom: 1px solid #e0e0e0; padding-bottom: 10px;\">Thông Tin Phòng</h3>" +

                            "<div style=\"display: flex; flex-wrap: wrap;\">" +

                            "<div style=\"flex: 1; min-width: 250px; margin-right: 20px;\">" +
                            "<table style=\"width: 100%; border-collapse: collapse;\">" +
                            "<tr>" +

                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Số phòng:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getRoomNumber() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Loại phòng:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getRoomType() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Diện tích:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getRoomSize() + " m²</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Giá thuê:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + String.format("%,.0f", roomRequest.getPrice()) + " VND/tháng</td>" +
                            "</tr>" +
                            "</table>" +
                            "</div>" +

                            "<div style=\"flex: 1; min-width: 250px;\">" +
                            "<table style=\"width: 100%; border-collapse: collapse;\">" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666; width: 140px;\"><strong>Thời hạn thuê:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getLeaseTerm() + " tháng</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Trạng thái:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getStatus() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Tình trạng:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + roomRequest.getCondition() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style=\"padding: 8px 0; color: #666;\"><strong>Địa chỉ:</strong></td>" +
                            "<td style=\"padding: 8px 0; color: #333;\">" + fullAddress + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</div>" +

                            "</div>" +

                            "<div style=\"margin-top: 20px;\">" +
                            "<h4 style=\"color: #3a3a3a; margin-bottom: 5px;\">Tiện ích:</h4>" +
                            facilitiesHtml +
                            "</div>" +

                            "<div style=\"margin-top: 20px;\">" +
                            "<h4 style=\"color: #3a3a3a; margin-bottom: 5px;\">Mô tả:</h4>" +
                            "<p style=\"margin-top: 5px; color: #666;\">" + roomRequest.getDescription() + "</p>" +
                            "</div>" +

                            "</div>" +

                            // Thông báo về tệp đính kèm
                            attachmentNotice +

                            "<div style=\"display: flex; justify-content: center; gap: 20px; margin: 30px 0;\">" +
                            "<a href=\"http://localhost:3000/admin/rooms/pending" + "\" style=\"background-color: #52c41a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; box-shadow: 0 2px 5px rgba(82, 196, 26, 0.3);\">Duyệt Bài Đăng</a>" +
                            "</div>" +

                            "<div style=\"background-color: #f9f9f9; padding: 12px; border-radius: 6px; margin-top: 20px; border-left: 4px solid #1890ff;\">" +
                            "<p style=\"color: #666; margin: 0; font-size: 14px;\"><strong>Lưu ý:</strong> Vui lòng kiểm tra kỹ thông tin và hình ảnh trước khi duyệt bài đăng. Nếu có bất kỳ thông tin không phù hợp, hãy từ chối và ghi rõ lý do.</p>" +
                            "</div>" +
                            "</div>" +

                            "<div style=\"text-align: center; padding-top: 15px; border-top: 2px solid #f2f2f2; font-size: 14px; color: #888;\">" +
                            "<p>Email này được gửi tự động từ hệ thống Quản lý Nhà trọ</p>" +
                            "<p style=\"margin-top: 5px;\">© " + new Date().getYear() + " 22DTHD5 - Mọi quyền được bảo lưu</p>" +
                            "</div>" +
                            "</div>",
                    true
            );

            // Thêm tệp đính kèm cho hình ảnh và video
            if (roomRequest.getMediaUrls() != null && !roomRequest.getMediaUrls().isEmpty()) {
                int attachmentIndex = 1;
                for (String mediaUrl : roomRequest.getMediaUrls()) {
                    try {
                        // Xác định loại tệp
                        boolean isVideo = mediaUrl.endsWith(".mp4") || mediaUrl.endsWith(".avi") || mediaUrl.endsWith(".mov");

                        // Lấy tên tệp từ URL
                        String fileName = getFileNameFromUrl(mediaUrl);

                        // Tạo tên tệp mô tả nếu không có tên
                        if (fileName == null || fileName.isEmpty()) {
                            fileName = "phong_" + roomRequest.getRoomNumber() + "_" + attachmentIndex +
                                    (isVideo ? ".mp4" : ".jpg");
                        }

                        // Tải xuống tệp từ URL
                        URL url = new URL(mediaUrl);
                        InputStream inputStream = url.openStream();

                        // Thêm tệp vào email dưới dạng đính kèm
                        helper.addAttachment(fileName, new InputStreamSource() {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return url.openStream();
                            }
                        });

                        attachmentIndex++;
                    } catch (IOException e) {
                        log.error("Error attaching media file: {}", e.getMessage());
                    }
                }
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error sending room approval request email: {}", e.getMessage());
        }
    }

    // Phương thức hỗ trợ để trích xuất tên tệp từ URL
    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Tìm tên tệp từ URL
        int lastPathSeparatorIndex = url.lastIndexOf('/');
        if (lastPathSeparatorIndex >= 0 && lastPathSeparatorIndex < url.length() - 1) {
            // Loại bỏ các tham số query nếu có
            String fileName = url.substring(lastPathSeparatorIndex + 1);
            int queryIndex = fileName.indexOf('?');
            if (queryIndex > 0) {
                fileName = fileName.substring(0, queryIndex);
            }
            return fileName;
        }

        return null;
    }
}
