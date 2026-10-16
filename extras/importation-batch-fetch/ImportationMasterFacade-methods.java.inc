public List<ImportationMaster> getExportRecordsByExtStatusAndAssessorWithFilter(ExternalStatus extStatus, UserMaster assessor, String tradeName, String exporterName,
            String manufacturerName, String importerName, String genericName, String mah,
            String importer, String importationId, String companyNameEn,
            String benificiaryName, String drugType, int firstRow, int rowCount) {
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            em.createNativeQuery("alter session set NLS_COMP=LINGUISTIC").executeUpdate();
            em.createNativeQuery("alter session set NLS_SORT=BINARY_CI").executeUpdate();

            CriteriaQuery cq = qb.createQuery();
            Root<ImportationMaster> pro = cq.from(ImportationMaster.class);
            List<Predicate> searchValues = new ArrayList<Predicate>();

            if (extStatus != null) {

                searchValues.add(qb.equal((pro.get("externalStatus").get("id")), extStatus.getId()));
            }
            if (assessor != null) {

                searchValues.add(qb.equal((pro.get("assessor")), assessor));
            }

            if (StringUtils.isNotEmpty(tradeName)) {
                String trade = "%" + tradeName + "%";
                searchValues.add(qb.like((pro.get("tradeName").as(String.class)), trade));
            }
            if (StringUtils.isNotEmpty(exporterName)) {
                String exporter = "%" + exporterName + "%";
                searchValues.add(qb.like(pro.get("exporterName").as(String.class), exporter));

            }
            if (StringUtils.isNotEmpty(manufacturerName)) {
                String manufacturer = "%" + manufacturerName + "%";
                searchValues.add(qb.like(pro.get("manufacturerName").as(String.class), manufacturer));
            }
            if (StringUtils.isNotEmpty(importerName)) {
                String importerN = "%" + importerName + "%";
                searchValues.add(qb.like(pro.get("importerName").as(String.class), importerN));
            }
            if (StringUtils.isNotEmpty(genericName)) {
                String genericN = "%" + genericName + "%";
                searchValues.add(qb.like(pro.get("genericName").as(String.class), genericN));
            }

            if (StringUtils.isNotEmpty(mah)) {

                String acc = "%" + mah + "%";
                searchValues.add(qb.like(pro.get("mah").as(String.class), acc));
            }

            if (StringUtils.isNotEmpty(importationId)) {

                String id = "%" + importationId + "%";
                searchValues.add(qb.like(pro.get("id").as(String.class), id));
            }

            if (StringUtils.isNotEmpty(companyNameEn)) {
                String companyName = "%" + companyNameEn + "%";
                searchValues.add(qb.like(pro.get("createdBy").get("companyNameEn").as(String.class), companyName));
            }

            if (StringUtils.isNotEmpty(benificiaryName)) {
                String beneficieryN = "%" + benificiaryName + "%";
                searchValues.add(qb.like(pro.get("beneficieryName").as(String.class), beneficieryN));
            }
            
            if (StringUtils.isNotEmpty(drugType)) {
                searchValues.add(qb.equal((pro.get("drugType").get("description")), drugType));
            }

            Predicate[] pre = searchValues.toArray(new Predicate[searchValues.size()]);

            cq.select(pro.get("id")).where(pre).orderBy(qb.desc(pro.get("submittedOn")), qb.desc(pro.get("id")));

            Query criteriaQuery = em.createQuery(cq);
            return loadImportationExportBatches(criteriaQuery, firstRow, rowCount);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load importations", e);
        }
    }

    public List<ImportationMaster> getExportRecordsWithFilter(String tradeName, String exporterName,
            String manufacturerName, String importerName, String genericName, String mah, String status, String importationId,
            String companyNameEn, String benificiaryName, String drugType, int firstRow, int rowCount) {
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            em.createNativeQuery("alter session set NLS_COMP=LINGUISTIC").executeUpdate();
            em.createNativeQuery("alter session set NLS_SORT=BINARY_CI").executeUpdate();

            CriteriaQuery cq = qb.createQuery();
            Root<ImportationMaster> pro = cq.from(ImportationMaster.class);
            List<Predicate> searchValues = new ArrayList<Predicate>();
            if (StringUtils.isNotEmpty(tradeName)) {
                String trade = "%" + tradeName + "%";
                searchValues.add(qb.like((pro.get("tradeName").as(String.class)), trade));
            }
            if (StringUtils.isNotEmpty(exporterName)) {
                String exporter = "%" + exporterName + "%";
                searchValues.add(qb.like(pro.get("exporterName").as(String.class), exporter));

            }
            if (StringUtils.isNotEmpty(manufacturerName)) {
                String manufacturer = "%" + manufacturerName + "%";
                searchValues.add(qb.like(pro.get("manufacturerName").as(String.class), manufacturer));
            }
            if (StringUtils.isNotEmpty(importerName)) {
                String importer = "%" + importerName + "%";
                searchValues.add(qb.like(pro.get("importerName").as(String.class), importer));
            }
            if (StringUtils.isNotEmpty(genericName)) {
                String genericN = "%" + genericName + "%";
                searchValues.add(qb.like(pro.get("genericName").as(String.class), genericN));
            }

            if (StringUtils.isNotEmpty(mah)) {

                String acc = "%" + mah + "%";
                searchValues.add(qb.like(pro.get("mah").as(String.class), acc));
            }

            if (StringUtils.isNotEmpty(status)) {
                String stat = "%" + status + "%";
                searchValues.add(qb.like(pro.get("externalStatus").get("descEn").as(String.class), stat));
            }

            if (StringUtils.isNotEmpty(importationId)) {

                String id = "%" + importationId + "%";
                searchValues.add(qb.like(pro.get("id").as(String.class), id));
            }

            if (StringUtils.isNotEmpty(companyNameEn)) {
                String companyName = "%" + companyNameEn + "%";
                searchValues.add(qb.like(pro.get("createdBy").get("companyNameEn").as(String.class), companyName));
            }

            if (StringUtils.isNotEmpty(benificiaryName)) {
                String beneficieryN = "%" + benificiaryName + "%";
                searchValues.add(qb.like(pro.get("beneficieryName").as(String.class), beneficieryN));
            }
            
            if (StringUtils.isNotEmpty(drugType)) {
                searchValues.add(qb.equal((pro.get("drugType").get("description")), drugType));
            }

            Predicate[] pre = searchValues.toArray(new Predicate[searchValues.size()]);

            cq.select(pro.get("id")).where(pre).orderBy(qb.desc(pro.get("submittedOn")), qb.desc(pro.get("id")));

            Query criteriaQuery = em.createQuery(cq);
            return loadImportationExportBatches(criteriaQuery, firstRow, rowCount);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load importations", e);
        }
    }
    /** Add inside ImportationMasterFacade along with the replacement methods. */
    @SuppressWarnings("unchecked")
    private List<ImportationMaster> loadImportationExportBatches(
            Query orderedIds, int firstRow, int rowCount) {
        if (firstRow < 0 || rowCount < 0) {
            throw new IllegalArgumentException("Pagination values must be nonnegative");
        }
        final int batchSize = 250;
        List<ImportationMaster> result = new ArrayList<ImportationMaster>();
        int loaded = 0;
        while (loaded < rowCount) {
            if (Thread.currentThread().isInterrupted()) {
                throw new java.util.concurrent.CancellationException("Export cancelled");
            }
            int requested = Math.min(batchSize, rowCount - loaded);
            long offset = (long) firstRow + loaded;
            if (offset > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Pagination offset exceeds JPA range");
            }
            List<Object> ids = orderedIds.setFirstResult((int) offset)
                    .setMaxResults(requested).getResultList();
            if (ids.isEmpty()) break;

            CriteriaBuilder cb = em.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery<ImportationMaster> data =
                    cb.createQuery(ImportationMaster.class);
            Root<ImportationMaster> item = data.from(ImportationMaster.class);
            String[] relationships = { "manufacturerCountry", "mahCountry",
                "dosageForm", "assessor", "assessorDecisionBy", "assessorDecision",
                "internalStatus", "submittedBy" };
            for (String relationship : relationships) {
                item.fetch(relationship, javax.persistence.criteria.JoinType.LEFT);
            }
            // Only one collection; pagination belongs to the ID query above.
            javax.persistence.criteria.Fetch<?, ?> approvals = item.fetch(
                    "approvals", javax.persistence.criteria.JoinType.LEFT);
            approvals.fetch("decision", javax.persistence.criteria.JoinType.LEFT);
            approvals.fetch("decisionBy", javax.persistence.criteria.JoinType.LEFT);
            data.select(item).distinct(true).where(item.get("id").in(ids));
            List<ImportationMaster> batch = em.createQuery(data).getResultList();
            java.util.Map<Object, ImportationMaster> byId =
                    new java.util.HashMap<Object, ImportationMaster>();
            for (ImportationMaster record : batch) byId.put(record.getId(), record);
            // IN does not preserve order. Restore the ID query's stable order.
            for (Object id : ids) {
                ImportationMaster record = byId.get(id);
                if (record != null) result.add(record);
            }
            loaded += ids.size();
            if (ids.size() < requested) break;
        }
        return result;
    }

