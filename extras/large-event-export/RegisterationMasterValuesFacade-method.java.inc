// Add inside your existing RegisterationMasterValuesFacade class.
// Fully qualified collection names avoid adding imports.
public java.util.Map<String, java.util.List<RegisterationMasterValues>>
        getValuesForExportBatch(java.util.List<RegisterationMaster> registrations) {
    java.util.Map<String, java.util.List<RegisterationMasterValues>> grouped =
            new java.util.HashMap<>();
    if (registrations == null || registrations.isEmpty()) { return grouped; }
    if (registrations.size() > 250) {
        throw new IllegalArgumentException("Export batch must contain at most 250 registrations");
    }

    // reg_Id.registrationId comes from your supplied deleteByRegId JPQL.
    // Verify fieldId against the entity mapping: JPQL uses Java attribute names.
    java.util.List<Object[]> results = em.createQuery(
            "SELECT v, v.reg_Id.registrationId "
            + "FROM RegisterationMasterValues v "
            + "LEFT JOIN FETCH v.fieldId "
            + "WHERE v.reg_Id IN :registrations "
            + "ORDER BY v.reg_Id.registrationId", Object[].class)
            .setParameter("registrations", registrations)
            .getResultList();
    for (Object[] result : results) {
        String registrationId = String.valueOf(result[1]);
        java.util.List<RegisterationMasterValues> values = grouped.get(registrationId);
        if (values == null) {
            values = new java.util.ArrayList<>();
            grouped.put(registrationId, values);
        }
        values.add((RegisterationMasterValues) result[0]);
    }
    return grouped;
}
