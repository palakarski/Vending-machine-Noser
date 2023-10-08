package com.example.demo.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "machines")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoinEntity> coins;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankNoteEntity> banknotes;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "products_to_sale",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "product_type")
    @Column(name = "quantity")
    private Map<ProductEntity, Integer> productForSale;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inserted_banknotes",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "banknote_type")
    @Column(name = "quantity")
    private Map<BankNoteEntity, Integer> insertedBankNotes;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inserted_coins",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "coin_type")
    @Column(name = "quantity")
    private Map<CoinEntity, Integer> insertedCoins;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coins_for_change",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "coin_type")
    @Column(name = "quantity")
    private Map<CoinEntity, Integer> coinsForChange;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coins_profit",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "coin_type")
    @Column(name = "quantity")
    private Map<CoinEntity, Integer> coinsProfit;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bank_notes_profit",
        joinColumns = @JoinColumn(name = "mashine_id"))
    @MapKeyColumn(name = "banknote_type")
    @Column(name = "quantity")
    private Map<BankNoteEntity, Integer> bankNotesProfit;

    @Transient
    private BigDecimal insertedSum;

    @Transient
    private BigDecimal amountForChange;

    @Transient
    private BigDecimal profit;

    public BigDecimal getAmountForChange() {
        amountForChange = BigDecimal.ZERO;
        for (CoinEntity coin : coinsForChange.keySet()) {
            var coinAmount = coin.getValue().multiply(BigDecimal.valueOf(coinsForChange.get(coin)));
            amountForChange = amountForChange.add(coinAmount);
        }
        return amountForChange;
    }

    public BigDecimal getInsertedSum() {
        BigDecimal coinsSum = BigDecimal.ZERO;
        for (Entry<CoinEntity, Integer> entry : insertedCoins.entrySet()) {
            coinsSum = coinsSum.add(entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        BigDecimal banknotesSum = BigDecimal.ZERO;
        for (Entry<BankNoteEntity, Integer> entry : insertedBankNotes.entrySet()) {
            banknotesSum = banknotesSum.add(entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return coinsSum.add(banknotesSum);
    }

    public BigDecimal getProfitSum() {
        profit = BigDecimal.ZERO;
        for (Entry<CoinEntity, Integer> entry : coinsProfit.entrySet()) {
            profit = profit.add(entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        BigDecimal banknotesSum = BigDecimal.ZERO;
        for (Entry<BankNoteEntity, Integer> entry : bankNotesProfit.entrySet()) {
            banknotesSum = banknotesSum.add(entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return profit.add(banknotesSum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MachineEntity)) {
            return false;
        }
        MachineEntity that = (MachineEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "MachineEntity{" +
            "id=" + id +
            ", products=" + products +
            ", coins=" + coins +
            ", banknotes=" + banknotes +
            ", productForSale=" + productForSale +
            ", insertedBankNotes=" + insertedBankNotes +
            ", insertedCoins=" + insertedCoins +
            ", coinsForChange=" + coinsForChange +
            ", coinsProfit=" + coinsProfit +
            ", bankNotesProfit=" + bankNotesProfit +
            ", insertedSum=" + insertedSum +
            ", amountForChange=" + amountForChange +
            '}';
    }
}
