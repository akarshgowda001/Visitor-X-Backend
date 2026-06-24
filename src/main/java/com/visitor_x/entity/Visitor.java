package com.visitor_x.entity;

import com.visitor_x.enums.PurposeOfVisit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitorId;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = false,length = 15)
    private String mobileNumber;

    @Column(nullable = false,length = 100)
    private String email;


    @Enumerated(EnumType.STRING)
    private PurposeOfVisit purposeOfVisit;

    //ai
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    @CreationTimestamp
    private LocalDateTime visitDateTime;
}