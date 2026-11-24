
package com.hourlyhire.discovery.serviceImpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hourlyhire.discovery.dto.WorkerSearchItemResponse;
import com.hourlyhire.discovery.dto.WorkerSearchRequest;
import com.hourlyhire.discovery.dto.WorkerSearchResponse;
import com.hourlyhire.discovery.entity.WorkerProfile;
import com.hourlyhire.discovery.repository.WorkerDiscoveryRepository;
import com.hourlyhire.discovery.service.WorkerDiscoveryService;

@Service
@Transactional(readOnly = true)
public class WorkerDiscoveryServiceImpl implements WorkerDiscoveryService {

    private final WorkerDiscoveryRepository workerDiscoveryRepository;

    public WorkerDiscoveryServiceImpl(
            WorkerDiscoveryRepository workerDiscoveryRepository) {
        this.workerDiscoveryRepository = workerDiscoveryRepository;
    }

    @Override
    public WorkerSearchResponse searchWorkers(WorkerSearchRequest request) {

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<WorkerProfile> workerPage =
                workerDiscoveryRepository.searchWorkers(
                        request.getSkillId(),
                        request.getServiceAreaId(),
                        pageable
                );

        List<WorkerSearchItemResponse> workers =
                workerPage.getContent()
                        .stream()
                        .map(worker -> new WorkerSearchItemResponse(
                                worker.getId(),
                                worker.getBio(),
                                worker.getProfilePhotoUrl(),
                                null,
                                0L
                        ))
                        .toList();

        return new WorkerSearchResponse(
                workers,
                workerPage.getNumber(),
                workerPage.getSize(),
                workerPage.getTotalElements(),
                workerPage.getTotalPages()
        );
    }
}
