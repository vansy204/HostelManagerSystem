package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.CreateInvoiceRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.entity.manager.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    Invoice toEntity(CreateInvoiceRequest request);

    @Mapping(target = "totalAmount", expression = "java(invoice.getRentAmount() + invoice.getElectricityAmount() + invoice.getWaterAmount() + invoice.getServiceAmount())")
    InvoiceResponse toResponse(Invoice invoice);
}
