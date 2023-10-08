package com.example.demo.model.entity;

import com.example.demo.enumeration.ProductSlotType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal price;
    private String productType;
    @Enumerated(EnumType.STRING)
    private ProductSlotType productSlot;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "machine_id")
    private MachineEntity machine;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductEntity)) {
            return false;
        }
        ProductEntity product = (ProductEntity) o;
        return Objects.equals(getId(), product.getId()) && Objects.equals(getPrice(), product.getPrice())
            && Objects.equals(getProductType(), product.getProductType()) && getProductSlot() == product.getProductSlot()
            && Objects.equals(getMachine(), product.getMachine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPrice(), getProductType(), getProductSlot(), getMachine());
    }
}
