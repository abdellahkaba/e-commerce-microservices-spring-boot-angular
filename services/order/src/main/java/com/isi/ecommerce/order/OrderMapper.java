package com.isi.ecommerce.order;


import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public Order toOrder(OrderRequest request) {

        if (request == null){
            return null ;
        }
        return Order.builder()
                .id(request.id())
                .customerId(request.customerId())
                .reference(request.reference())
                .totalAmount(request.amount())
                .paymentMethod(request.paymentMethod())
                .build();

    }

    public OrderResponse fromOrder(Order order) {
         return new OrderResponse(
                 order.getId(),
                 order.getReference(),
                 order.getTotalAmount(),
                 order.getPaymentMethod(),
                 order.getCustomerId()
         );
    }
}
