package com.kpanov.clicker.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users_balance_table")
public class BalanceEntity {

    @Id @Column(name = "user_id")
    private Integer userId;

    private Integer balance;

    @Column(name = "user_nickname")
    private Integer userNickname;

    @Column(name = "user_email")
    private Integer userEmail;
}