package com.isi.ecommerce.order;


import com.isi.ecommerce.customer.CustomerClient;
import com.isi.ecommerce.exception.BusinessException;
import com.isi.ecommerce.kafka.OrderConfirmation;
import com.isi.ecommerce.kafka.OrderProducer;
import com.isi.ecommerce.orderLine.OrderLineRequest;
import com.isi.ecommerce.orderLine.OrderLineService;
import com.isi.ecommerce.payment.PaymentClient;
import com.isi.ecommerce.payment.PaymentRequest;
import com.isi.ecommerce.product.ProductClient;
import com.isi.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient ;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;


    public Integer createOrder(OrderRequest request) {

        // check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provide ID ::" + request.customerId()));

        // purchase the products ---> product-ms (RestTemplate)
       var purchasedProducts = this.productClient.purchaseProducts(request.products());
        // persiste order
        var order = this.repository.save(mapper.toOrder(request));
        // persist order lines
        for (PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // todo start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);
        // send the order confirmation ---> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID : %s" + orderId)));
    }
}
