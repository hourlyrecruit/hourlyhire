
package com.hourlyhire.discovery.service;

import com.hourlyhire.discovery.dto.WorkerSearchRequest;
import com.hourlyhire.discovery.dto.WorkerSearchResponse;

public interface WorkerDiscoveryService {

    WorkerSearchResponse searchWorkers(WorkerSearchRequest request);
}
