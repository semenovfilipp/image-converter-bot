package org.semenov.dao;

import org.semenov.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDao extends JpaRepository<AppDocument, Long> {
}
