package org.benetech.servicenet.service;

import org.benetech.servicenet.domain.Contact;
import org.benetech.servicenet.service.dto.ContactDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing Contact.
 */
public interface ContactService {

    /**
     * Save a contact.
     *
     * @param contactDTO the entity to save
     * @return the persisted entity
     */
    ContactDTO save(ContactDTO contactDTO);

    /**
     * Get all the contacts.
     *
     * @return the list of entities
     */
    List<ContactDTO> findAll();

    /**
     * Get all the contacts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ContactDTO> findAll(Pageable pageable);

    /**
     * Get the "id" contact.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ContactDTO> findOne(UUID id);

    Optional<Contact> findForExternalDb(String externalDbId, String providerNae);

    /**
     * Delete the "id" contact.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);
}
