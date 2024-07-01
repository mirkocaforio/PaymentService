package it.unisalento.pasproject.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * The UserDetailsDTO class is a Data Transfer Object (DTO) that represents the details of a user.
 * It includes properties such as email, role, and enabled status.
 * It also includes getter and setter methods for these properties.
 */
@Getter
@Setter
public class UserDetailsDTO {
    /**
     * The email of the user.
     */
    private String email;

    /**
     * The role of the user.
     */
    private String role;

    /**
     * The enabled status of the user.
     */
    private Boolean enabled;

    /**
     * Default constructor for UserDetailsDTO.
     */
    public UserDetailsDTO() {}

    /**
     * Constructor for UserDetailsDTO with email, role, and enabled status.
     * @param email the email of the user
     * @param role the role of the user
     * @param enable the enabled status of the user
     */
    public UserDetailsDTO(String email, String role, Boolean enable) {
        this.email = email;
        this.role = role;
        this.enabled = true;
    }
}
