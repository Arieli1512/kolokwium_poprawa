package edu.iis.mto.testreactor.reservation;

import edu.iis.mto.testreactor.money.Money;
import edu.iis.mto.testreactor.offer.Discount;
import edu.iis.mto.testreactor.offer.DiscountPolicy;

import edu.iis.mto.testreactor.offer.Offer;
import edu.iis.mto.testreactor.offer.OfferItem;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ReservationTest {
    @Mock
    DiscountPolicy discountPolicy;
    ClientData clientData;
    Date date;

    @BeforeEach
    void setUp() throws Exception {
        clientData = new ClientData(new Id("Client"),"Client");
        date = new Date();

    }


    @Test
    void reservationShouldContainProduct()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,clientData,date);
        Money money = new Money(100);
        Product product = new Product(Id.generate(),money,"Food1",ProductType.FOOD);

//        ReservedProduct reservedProduct = new ReservedProduct(product.getId(),product.getName(),1,money);
//
//        ArrayList<ReservedProduct> reservationItemsExpected = new ArrayList<>();
//        reservationItemsExpected.add(reservedProduct);

        reservation.add(product,1);
        assertEquals(true,  reservation.contains(product));
//        assertEquals(reservationItemsExpected,reservation.getReservedProducts());

    }
    @Test
    void reservationShouldNotContainProduct()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,clientData,date);
        Money money = new Money(100);
        Product product = new Product(Id.generate(),money,"Food1",ProductType.FOOD);
        assertEquals(false,  reservation.contains(product));
    }

    @Test
    void getStatusReturnOPENED()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,clientData,date);
        assertEquals(Reservation.ReservationStatus.OPENED,reservation.getStatus());
    }

    @Test
    void reservationIsClosed()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.CLOSED,clientData,date);
        assertEquals(true,reservation.isClosed());
    }

    @Test
    void closeShouldReturnErrorWhenAlreadyClosed()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.CLOSED,clientData,date);
        assertThrows(DomainOperationException.class, () -> reservation.close());
    }

    @Test
    void addShouldThrowExceptionWhenClosed()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.CLOSED,clientData,date);
        Money money = new Money(100);
        Product product = new Product(Id.generate(),money,"Food1",ProductType.FOOD);

        assertThrows(DomainOperationException.class, () -> reservation.add(product,1));
    }

    @Test
    void calculateOfferReturnsValidOfferWithItemsAvailable()
    {
        Reservation reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,clientData,date);
        Money money = new Money(100);
        Product product = new Product(Id.generate(),money,"Food1",ProductType.FOOD);
        Discount discount = new Discount("Discount",new Money(10));

        ArrayList<OfferItem> avabile = new ArrayList<>();

        ProductData productData = new ProductData(product.getId(),product.getPrice(),product.getName(),product.getProductType(),product.generateSnapshot().getSnapshotDate());
        OfferItem offerItem = new OfferItem(productData,1);
        reservation.add(product,1);
        ArrayList<OfferItem> unavabile = new ArrayList<>();
        avabile.add(offerItem);
        Offer offer = new Offer(avabile,unavabile);
        assertEquals(offer.getAvailabeItems(),reservation.calculateOffer(discountPolicy).getAvailabeItems());
        assertEquals(offer.getUnavailableItems(),reservation.calculateOffer(discountPolicy).getUnavailableItems());
    }


}
