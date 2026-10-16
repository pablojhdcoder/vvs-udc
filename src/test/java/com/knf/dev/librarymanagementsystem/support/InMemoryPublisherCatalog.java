package com.knf.dev.librarymanagementsystem.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.impl.PublisherServiceImpl;

public final class InMemoryPublisherCatalog {

  private final List<Publisher> publishers = new ArrayList<>();
  private final AtomicLong nextId = new AtomicLong(1);
  private final PublisherRepository repository;
  private final PublisherServiceImpl service;

  public InMemoryPublisherCatalog() {
    repository = mock(PublisherRepository.class);
    service = new PublisherServiceImpl(repository);
    when(repository.findAll()).thenAnswer(invocation -> new ArrayList<>(publishers));
    when(repository.findById(anyLong())).thenAnswer(invocation -> {
      Long id = invocation.getArgument(0);
      return publishers.stream().filter(publisher -> id.equals(publisher.getId())).findFirst();
    });
    when(repository.save(any(Publisher.class))).thenAnswer(invocation -> {
      Publisher publisher = invocation.getArgument(0);
      if (publisher.getId() == null) {
        publisher.setId(nextId.getAndIncrement());
      }
      publishers.removeIf(existing -> publisher.getId().equals(existing.getId()));
      publishers.add(publisher);
      return publisher;
    });
    doAnswer(invocation -> {
      Long id = invocation.getArgument(0);
      publishers.removeIf(publisher -> id.equals(publisher.getId()));
      return null;
    }).when(repository).deleteById(anyLong());
  }

  public PublisherServiceImpl service() {
    return service;
  }

  public PublisherRepository repository() {
    return repository;
  }

  public int size() {
    return publishers.size();
  }
}