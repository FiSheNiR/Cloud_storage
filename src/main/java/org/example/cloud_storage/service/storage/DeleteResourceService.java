package org.example.cloud_storage.service.storage;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.service.MinioService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteResourceService {

    private final MinioService minioService;

    public void deleteResource(String path, String username) {
        if (path.endsWith("/")) {
            Iterable<Result<Item>> items = minioService.directoryInfo(path, username, false);
            for (Result<Item> resultItem : items) {
                try {
                    Item item = resultItem.get();
                    String objectName = item.objectName();
                    if (objectName.equals(path)) {
                        continue;
                    }
                    deleteResource(objectName, username);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            minioService.deleteResource(path, username);
        } else {
            minioService.deleteResource(path, username);
        }
    }
}
