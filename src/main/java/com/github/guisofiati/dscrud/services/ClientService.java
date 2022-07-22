package com.github.guisofiati.dscrud.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.guisofiati.dscrud.dto.ClientDTO;
import com.github.guisofiati.dscrud.entities.Client;
import com.github.guisofiati.dscrud.repositories.ClientRepository;
import com.github.guisofiati.dscrud.services.exceptions.DatabaseException;
import com.github.guisofiati.dscrud.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {
	
	@Autowired
	private ClientRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(Pageable pageable) {
		Page<Client> list = repository.findAll(pageable); // page ja é stream
		return list.map(x -> new ClientDTO(x));
	}
	
	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Optional<Client> obj = repository.findById(id);
		// pode nao existir o id, entao em vez do get, usar o orelsethrow
		Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource id " + id + " not found"));
		return new ClientDTO(entity);
	}
	
	@Transactional(readOnly = true)
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
			entity.setName(dto.getName());
			entity.setCpf(dto.getCpf());
			entity.setIncome(dto.getIncome());
			entity.setBirthDate(dto.getBirthDate());
			entity.setChildren(dto.getChildren());
			repository.save(entity);
			return new ClientDTO(entity);
		}
	
	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {
			// verificar se primeiro existe
			Client entity = repository.getReferenceById(id);
			entity.setName(dto.getName());
			entity.setCpf(dto.getCpf());
			entity.setIncome(dto.getIncome());
			entity.setBirthDate(dto.getBirthDate());
			entity.setChildren(dto.getChildren());
			repository.save(entity);
			return new ClientDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Failed to update resource. Resource id " + id + " not found");
		}
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Failed to delete resource. Id " + id + " not found");
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
}
