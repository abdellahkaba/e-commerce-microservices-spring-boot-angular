package com.isi.ecommerce.kafka;

import com.isi.ecommerce.customer.CustomerResponse;
import com.isi.ecommerce.order.PaymentMethod;
import com.isi.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod payementMethode,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
