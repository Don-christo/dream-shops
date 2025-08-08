package com.codewithiyke.dreamshops.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //  @Version private Long version;

  private BigDecimal totalAmount = BigDecimal.ZERO;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<CartItem> items = new HashSet<>();

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  public void addItem(CartItem item) {
    this.items.add(item);
    item.setCart(this);
    updateTotalAmount();
  }

  public void removeItem(CartItem item) {
    this.items.remove(item);
    item.setCart(null);
    updateTotalAmount();
  }

  private void updateTotalAmount() {
    this.totalAmount =
        items.stream()
            .map(
                item -> {
                  BigDecimal unitPrice = item.getUnitPrice();
                  if (unitPrice == null) {
                    return BigDecimal.ZERO;
                  }
                  return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
