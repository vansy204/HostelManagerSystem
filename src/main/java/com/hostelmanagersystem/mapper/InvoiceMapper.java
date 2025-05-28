package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.entity.manager.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    Invoice toInvoice(InvoiceCreateRequest request, String ownerId);

    InvoiceResponse toInvoiceResponse(Invoice invoice);
}
