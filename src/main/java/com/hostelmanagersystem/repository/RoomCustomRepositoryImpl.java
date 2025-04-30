package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomCustomRepositoryImpl implements RoomCustomRepository {

    private final MongoTemplate mongoTemplate;

    public RoomCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Room> findByFilters(Double minPrice,
                                    Double maxPrice,
                                    Double minSize,
                                    Double maxSize,
                                    String status,
                                    String roomType,
                                    List<String> facilities,
                                    Integer leaseTerm,
                                    String condition) {

        List<Criteria> criteriaList = new ArrayList<>();

        // Lọc theo giá
        if (minPrice != null) criteriaList.add(Criteria.where("price").gte(minPrice));
        if (maxPrice != null) criteriaList.add(Criteria.where("price").lte(maxPrice));

        // Lọc theo diện tích
        if (minSize != null) criteriaList.add(Criteria.where("roomSize").gte(minSize));
        if (maxSize != null) criteriaList.add(Criteria.where("roomSize").lte(maxSize));

        // Lọc theo trạng thái phòng
        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(RoomStatus.valueOf(status.toUpperCase())));
        }

        // Lọc theo loại phòng
        if (roomType != null && !roomType.isBlank()) {
            criteriaList.add(Criteria.where("roomType").is(roomType));
        }

        // Lọc theo tiện ích (facilities)
        if (facilities != null && !facilities.isEmpty()) {
            criteriaList.add(Criteria.where("facilities").in(facilities));
        }

        // Lọc theo thời gian thuê (leaseTerm)
        if (leaseTerm != null) {
            criteriaList.add(Criteria.where("leaseTerm").gte(leaseTerm));
        }

        // Lọc theo tình trạng phòng
        if (condition != null && !condition.isBlank()) {
            criteriaList.add(Criteria.where("condition").is(condition));
        }

        // Kết hợp các điều kiện lọc
        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Room.class);
    }
}

