package com.example.demo.model.entity;

import com.example.demo.enumeration.BankNoteValue;
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
@Table(name = "bank_notes")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BankNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private BankNoteValue type;
    private BigDecimal value;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "machine_id")
    private MachineEntity machine;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankNoteEntity)) {
            return false;
        }
        BankNoteEntity that = (BankNoteEntity) o;
        return Objects.equals(getId(), that.getId()) && getType() == that.getType() && Objects.equals(getValue(),
            that.getValue()) && Objects.equals(getMachine(), that.getMachine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getValue(), getMachine());
    }
}
