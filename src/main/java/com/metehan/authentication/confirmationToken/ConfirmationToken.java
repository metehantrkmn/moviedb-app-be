package com.metehan.authentication.confirmationToken;

import com.metehan.authentication.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Date calculateExpirationDate(){
        var miliseconds = createdDate.getTime() + (1000*60*3);
        Date expirationDate = new Date(miliseconds);
        return expirationDate;
    }

}
