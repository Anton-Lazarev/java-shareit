package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Paginator extends PageRequest {
    public Paginator(int from, int size) {
        super(from / size, size, Sort.unsorted());
    }
}
