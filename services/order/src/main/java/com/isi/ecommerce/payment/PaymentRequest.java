package com.isi.ecommerce.payment;

import com.isi.ecommerce.customer.CustomerResponse;
import com.isi.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
     BigDecimal amount,
     PaymentMethod paymentMethod,
     Integer orderId,
     String orderReference,
     CustomerResponse customer
) {
}
