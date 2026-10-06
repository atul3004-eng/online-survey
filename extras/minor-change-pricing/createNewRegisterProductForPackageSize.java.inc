// Replacement method for MinorChangeController; paste inside the existing class.
// Pass directorFlow=false for section-head finalization.
// Pass directorFlow=true only for retained director-pending requests.
// Populate finalDecision, finalDecisionBy and finalDecisionOn before calling.
// Propagate directorFlow through all calling product-update methods.

    private void createNewRegisterProductForPackageSize(RegisterProductMaster existingProduct,
            String newProductPackSize, EntityManager em, MinorChange minorChange, UserMaster currentUser, boolean directorFlow) throws Exception {
        try {

            //registerProductMasterController.ejbFacade.detach(newProduct);
            RegisterProductMaster newProduct = new RegisterProductMaster();
            newProduct.setId(null);
            newProduct.setMah(existingProduct.getMah());
            newProduct.setAgent(existingProduct.getAgent());
            newProduct.setTradeNameEn(existingProduct.getTradeNameEn());
            newProduct.setTradeNameAr(existingProduct.getTradeNameAr());
            newProduct.setPharmacologicalGroup(existingProduct.getPharmacologicalGroup());
            newProduct.setDosageForm(existingProduct.getDosageForm());
            newProduct.setOthrDosageForm(existingProduct.getOthrDosageForm());
            newProduct.setPackageType(existingProduct.getPackageType());
            newProduct.setOthrPackageType(existingProduct.getOthrPackageType());
            newProduct.setDetailedContainerCloserSystem(existingProduct.getDetailedContainerCloserSystem());
            newProduct.setStorageConditions(existingProduct.getStorageConditions());
            newProduct.setShelfLife(existingProduct.getShelfLife());
            newProduct.setRetailPrice(existingProduct.getRetailPrice());
            newProduct.setCifPrice(existingProduct.getCifPrice());
            newProduct.setWholesalePrice(existingProduct.getWholesalePrice());
            newProduct.setCreatedBy(existingProduct.getCreatedBy());
            newProduct.setCreatedOn(new Timestamp((new Date()).getTime()));
            newProduct.setExternalStatus(existingProduct.getExternalStatus());
            newProduct.setProductClassification(existingProduct.getProductClassification());
            newProduct.setSubmittedBy(minorChange.getCreatedBy());
            newProduct.setSubmittedOn(minorChange.getSubmittedOn());
            newProduct.setIndication(existingProduct.getIndication());
            newProduct.setTherapyDuration(existingProduct.getTherapyDuration());
            newProduct.setWarningPrecautions(existingProduct.getWarningPrecautions());
            newProduct.setEffectAntitode(existingProduct.getEffectAntitode());
            newProduct.setContraIndication(existingProduct.getContraIndication());
            newProduct.setInternalStatus(existingProduct.getInternalStatus());
            newProduct.setApprovedBy(currentUser);
            newProduct.setApprovedOn(new Timestamp((new Date()).getTime()));
            newProduct.setLeafletRevisionDate(existingProduct.getLeafletRevisionDate());
            newProduct.setBatchReleaser(existingProduct.getBatchReleaser());
            newProduct.setActive(existingProduct.isActive());
            newProduct.setAtcCode(existingProduct.getAtcCode());
            newProduct.setDispensingMode(existingProduct.getDispensingMode());
            newProduct.setDoseRouteAdmin(existingProduct.getDoseRouteAdmin());
            newProduct.setUnderLicenseDetail(existingProduct.getUnderLicenseDetail());
            newProduct.setUnderLicense(existingProduct.isUnderLicense());
            newProduct.setApplicationType(existingProduct.getApplicationType());
            newProduct.setProductCodeNo(existingProduct.getProductCodeNo());
            newProduct.setNewRegistration(existingProduct.isNewRegistration());
            newProduct.setRegExpiryDate(existingProduct.getRegExpiryDate());

            newProduct.setPackageSize(newProductPackSize);
            String regNo = existingProduct.getRegistrationNo();
            if (StringUtils.countMatches(regNo, "-") > 1) {
                regNo = StringUtils.substringBeforeLast(regNo, "-").trim();
            }
            for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
                if (registerProductMasterController.ejbFacade.getRecordByRegistrationNo(regNo + "-" + alphabet) == null) {
                    newProduct.setRegistrationNo(existingProduct.getRegistrationNo() + "-" + alphabet);
                    break;
                }
            }
            System.out.println("Creating new product with registration no.: " + newProduct.getRegistrationNo());
            List<APIMaster> apis = new ArrayList<>();
            for (APIMaster api : existingProduct.getApi()) {
                //api.setId(null);
                APIMaster napi = new APIMaster();
                napi.setConcentration(api.getConcentration());
                napi.setEquivalency(api.getEquivalency());
                napi.setIngredients(api.getIngredients());
                napi.setManufacturer(api.getManufacturer());
                napi.setManufacturer2(api.getManufacturer2());
                napi.setRegisterProductMaster(newProduct);
                apis.add(napi);
            }
            if (!apis.isEmpty()) {
                newProduct.setApi(apis);
            }

            List<PrimaryPackager> pps = new ArrayList<>();
            for (PrimaryPackager pp : existingProduct.getPrimaryPackagers()) {
                PrimaryPackager npp = new PrimaryPackager();
                //pp.setId(null);
                npp.setAddress(pp.getAddress());
                npp.setManufacturer(pp.getManufacturer());
                npp.setPackager(pp.getPackager());
                npp.setRegisterProductMaster(newProduct);
                pps.add(npp);
            }
            if (!pps.isEmpty()) {
                newProduct.setPrimaryPackagers(pps);
            }

            List<SecondaryPackager> sps = new ArrayList<>();
            for (SecondaryPackager sp : existingProduct.getSecondaryPackagers()) {
                SecondaryPackager nsp = new SecondaryPackager();
                nsp.setAddress(sp.getAddress());
                nsp.setManufacturer(sp.getManufacturer());
                nsp.setPackager(sp.getPackager());
                //sp.setId(null);
                nsp.setRegisterProductMaster(newProduct);
                sps.add(nsp);
            }
            if (!sps.isEmpty()) {
                newProduct.setSecondaryPackagers(sps);
            }

            List<BulkManufacturer> bms = new ArrayList<>();
            for (BulkManufacturer bm : existingProduct.getBulkManufacturers()) {
                //bm.setId(null);
                BulkManufacturer nbm = new BulkManufacturer();
                nbm.setAddress(bm.getAddress());
                nbm.setManufact(bm.getManufact());
                nbm.setManufacturer(bm.getManufacturer());
                nbm.setRegisterProductMaster(newProduct);
                bms.add(nbm);
            }
            if (!bms.isEmpty()) {
                newProduct.setBulkManufacturers(bms);
            }

            List<RegisterProductDocument> docs = new ArrayList<>();
            for (RegisterProductDocument doc : existingProduct.getDocuments()) {
                RegisterProductDocument ndoc = new RegisterProductDocument();
                //doc.setId(null);
                ndoc.setDocumentName(doc.getDocumentName());
                ndoc.setDocumentPath(doc.getDocumentPath());
                ndoc.setFileName(doc.getFileName());
                ndoc.setMandatory(doc.isMandatory());
                ndoc.setSerialNo(doc.getSerialNo());
                ndoc.setUploadedBy(doc.getUploadedBy());
                ndoc.setUploadedOn(doc.getUploadedOn());
                ndoc.setVisibleToAgent(doc.isVisibleToAgent());
                ndoc.setRegisterProductMaster(newProduct);
                docs.add(ndoc);
            }
            if (!docs.isEmpty()) {
                newProduct.setDocuments(docs);
            }

            List<ProductRegistrationIngredient> pris = new ArrayList<>();
            for (ProductRegistrationIngredient pri : existingProduct.getIngredients()) {
                //pri.setId(null);
                ProductRegistrationIngredient npri = new ProductRegistrationIngredient();
                npri.setActive(pri.isActive());
                npri.setConcentration(pri.getConcentration());
                npri.setDrugfunction(pri.getDrugfunction());
                npri.setIngredient(pri.getIngredient());
                npri.setProduct(newProduct);
                pris.add(npri);
            }
            if (!pris.isEmpty()) {
                newProduct.setIngredients(pris);
            }

            List<GccRegistration> gccs = new ArrayList<>();
            for (GccRegistration gcc : existingProduct.getGccRegistrations()) {
                //gcc.setId(null);
                GccRegistration ngcc = new GccRegistration();
                ngcc.setGccCountry(gcc.getGccCountry());
                ngcc.setOtherCountryName(gcc.getOtherCountryName());
                ngcc.setRegistrationDate(gcc.getRegistrationDate());
                ngcc.setRegistrationNo(gcc.getRegistrationNo());
                ngcc.setRenewalDate(gcc.getRenewalDate());
                ngcc.setRegisterProductMaster(newProduct);
                gccs.add(ngcc);
            }
            if (!gccs.isEmpty()) {
                newProduct.setGccRegistrations(gccs);
            }

            List<ProductSampleMaster> psms = new ArrayList<>();
            for (ProductSampleMaster psm : existingProduct.getSamples()) {
                //psm.setId(null);
                ProductSampleMaster npsm = new ProductSampleMaster();
                npsm.setBatchNo(psm.getBatchNo());
                npsm.setCoaDocPath(psm.getCoaDocPath());
                npsm.setCreatedBy(psm.getCreatedBy());
                npsm.setCreatedOn(psm.getCreatedOn());
                npsm.setExpiryDate(psm.getExpiryDate());
                npsm.setManufactureDate(psm.getManufactureDate());
                npsm.setPackSize(psm.getPackSize());
                npsm.setSampleCount(psm.getSampleCount());
                npsm.setRegisterProductMaster(newProduct);
                psms.add(npsm);
            }
            if (!psms.isEmpty()) {
                newProduct.setSamples(psms);
            }

            /* List<ProductReportMaster> prms = new ArrayList<>();
            for (ProductReportMaster prm : existingProduct.getReports()) {
                prm.setId(null);
                prm.setRegisterProductMaster(newProduct);
                for (SimilarProductMaster similarProduct : prm.getSimilarProducts()) {
                    similarProduct.setId(null);
                    similarProduct.setReport(prm);
                }
                prms.add(prm);
            }
            if (!prms.isEmpty()) {
                newProduct.setReports(prms);
            }

            List<RegisterProductSubmission> submissions = new ArrayList<>();
            for (RegisterProductSubmission submission : existingProduct.getSubmissions()) {
                submission.setId(null);
                submission.setRegisterProductMaster(newProduct);
                submissions.add(submission);
            }
            if (!submissions.isEmpty()) {
                newProduct.setSubmissions(submissions);
            }*/
            List<ProductReferenceStandard> standards = new ArrayList<>();
            for (ProductReferenceStandard standard : existingProduct.getStandards()) {
                //standard.setId(null);
                ProductReferenceStandard ns = new ProductReferenceStandard();
                ns.setBatchNo(standard.getBatchNo());
                ns.setCoaDocPath(standard.getCoaDocPath());
                ns.setCreatedBy(standard.getCreatedBy());
                ns.setCreatedOn(standard.getCreatedOn());
                ns.setExpiryDate(standard.getExpiryDate());
                ns.setManufactureDate(standard.getManufactureDate());
                ns.setSampleCount(standard.getSampleCount());
                ns.setStandardName(standard.getStandardName());
                ns.setStorageConditions(standard.getStorageConditions());
                ns.setRegisterProductMaster(newProduct);
                standards.add(ns);
            }
            if (!standards.isEmpty()) {
                newProduct.setStandards(standards);
            }

            List<ProductBatchReleaser> brs = new ArrayList<>();
            for (ProductBatchReleaser br : existingProduct.getBatchReleasers()) {
                //br.setId(null);
                ProductBatchReleaser nbr = new ProductBatchReleaser();
                nbr.setManufacturer(br.getManufacturer());
                nbr.setRegisterProductMaster(newProduct);
                brs.add(nbr);
            }
            if (!brs.isEmpty()) {
                newProduct.setBatchReleasers(brs);
            }

            if (existingProduct.getMeetings() != null && !existingProduct.getMeetings().isEmpty()) {
                for (RegistrationCommitteeMeeting mtng : existingProduct.getMeetings()) {
                    RegistrationMeetingDecision existingDecision = registrationMeetingDecisionFacade.getRecordByRegistrationAndMeetingForDecision(minorChange.getRegisterProductMaster(),
                            mtng);
                    if (existingDecision != null) {
                        RegistrationMeetingDecision meetingDecision = new RegistrationMeetingDecision();
                        meetingDecision.setDecision(existingDecision.getDecision());
                        meetingDecision.setDecisionBy(existingDecision.getDecisionBy());
                        meetingDecision.setDecisionOn(existingDecision.getDecisionOn());
                        meetingDecision.setRemarks(existingDecision.getRemarks());
                        meetingDecision.setMeeting(mtng);
                        meetingDecision.setRegisterProduct(newProduct);
                        if (existingDecision.getDecision().getId() == 4) {
                            newProduct.setMeetingNo(mtng.getMeetingSerialNo());
                            newProduct.setMeetingDate(mtng.getMeetingDate());
                        }
                    }
                }
            }
            if (minorChange.getAssignee() != null) {
                newProduct.setAssigneeName(minorChange.getAssignee().getFirstNameEn() + " " + minorChange.getAssignee().getLastNameEn());
            }

            newProduct.setMeetings(null);
            newProduct.setAgentHistory(null);
            newProduct.setCancellations(null);
            newProduct.setHerbalPriceMasterHistory(null);
            newProduct.setHerbalPriceMasters(null);
            newProduct.setHerbalPriceUpdations(null);
            newProduct.setPriceUpdationHistory(null);
            newProduct.setReRegistrations(null);

            //ProductPriceUpdation existingPriceUpdate = existingProduct.getPriceUpdation();
            newProduct.setPriceUpdation(null);
            newProduct.setPriceUpdationHistory(null);

            java.util.Date date = new java.util.Date();
            newProduct.setCreatedOn(new Timestamp(date.getTime()));
            /*ProductPriceUpdation priceUpdate = null;
            for (MinorChangeProductPricing pricing : minorChange.getProductPriceUpdations()) {
                if (pricing.getPackageSize().equals(newProduct.getPackageSize())) {
                    priceUpdate = pricing.getPriceUpdation();
                    newProduct.setRetailPrice(StringUtils.isNotBlank(priceUpdate.getNewRetailPrice())
                            ? priceUpdate.getNewRetailPrice() : newProduct.getRetailPrice());
                    newProduct.setWholesalePrice(StringUtils.isNotBlank(priceUpdate.getNewWholesalePrice())
                            ? priceUpdate.getNewWholesalePrice() : newProduct.getWholesalePrice());
                    newProduct.setCifPrice(StringUtils.isNotBlank(priceUpdate.getNewCifPrice())
                            ? priceUpdate.getNewCifPrice() : newProduct.getCifPrice());
                    newProduct.setPriceUpdation(priceUpdate);
                    break;
                }
            }

            if (newProduct.getPriceUpdation() == null) {
                newProduct.setPriceUpdation(existingPriceUpdate);
            }*/

            RegisterProductApproval headApproval = new RegisterProductApproval();
            headApproval.setSectionHead(true);
            headApproval.setRegisterProductMaster(newProduct);
            headApproval.setDecisionBy(minorChange.getSectionHead());
            headApproval.setDecisionOn(minorChange.getHeadDecisionTakenOn());
            headApproval.setDecision(minorChange.getHeadDecision());

            newProduct.setHeadDecision(minorChange.getHeadDecision());
            newProduct.setHeadDecisionBy(minorChange.getSectionHead());
            newProduct.setHeadDecisionOn(minorChange.getHeadDecisionTakenOn());

            List<RegisterProductApproval> newApprovals = new ArrayList<>();
            newApprovals.add(headApproval);

            if (directorFlow) {
                // Existing director-pending requests retain their director approval.
                RegisterProductApproval directorApproval = new RegisterProductApproval();
                directorApproval.setDirector(true);
                directorApproval.setRegisterProductMaster(newProduct);
                directorApproval.setDecisionBy(minorChange.getFinalDecisionBy());
                directorApproval.setDecisionOn(minorChange.getFinalDecisionOn());
                directorApproval.setRemarks(directorRemarks);
                directorApproval.setDecision(minorChange.getFinalDecision());
                newApprovals.add(directorApproval);
                newProduct.setDirectorDecision(minorChange.getFinalDecision());
            } else {
                // Section head performs the final decision for the new flow.
                headApproval.setDecision(minorChange.getFinalDecision());
                headApproval.setDecisionBy(minorChange.getFinalDecisionBy());
                headApproval.setDecisionOn(minorChange.getFinalDecisionOn());
                headApproval.setRemarks(headRemarks);
                newProduct.setHeadDecision(minorChange.getFinalDecision());
                newProduct.setHeadDecisionBy(minorChange.getFinalDecisionBy());
                newProduct.setHeadDecisionOn(minorChange.getFinalDecisionOn());
            }
            newProduct.setApprovals(newApprovals);

            List<ProductAssigneeMaster> newAssignees = new ArrayList<>();
            for (MinorChangeApproval mcApproval : minorChange.getApprovals()) {
                if (mcApproval.isAssessor()) {
                    ProductAssigneeMaster newAssignee = new ProductAssigneeMaster();
                    newAssignee.setRegisterProductMaster(newProduct);
                    newAssignee.setAssigned("t");
                    newAssignee.setAssignedBy(mcApproval.getAssignedBy());
                    newAssignee.setAssignedOn(mcApproval.getAssignedOn());
                    newAssignee.setAssignedTo(mcApproval.getDecisionBy());
                    newAssignee.setDecision(mcApproval.getDecision());
                    newAssignee.setDecisionTakenOn(mcApproval.getDecisionOn());
                    newAssignee.setRemarks(mcApproval.getRemarks());
                    newAssignees.add(newAssignee);
                }
            }
            newProduct.setAssignees(newAssignees);

            em.persist(newProduct);

            MinorChangeProductPricing pricing = new MinorChangeProductPricing();
            pricing.setPackageSize(newProduct.getPackageSize());
            ProductPriceUpdation price = new ProductPriceUpdation();
            price.setOldCifPrice(newProduct.getCifPrice());
            price.setOldRetailPrice(newProduct.getRetailPrice());
            price.setOldWholesalePrice(newProduct.getWholesalePrice());
            pricing.setPriceUpdation(price);
            pricing.setMinorChange(minorChange);

            PricingMaster pricingMaster = new PricingMaster();
            pricingMaster.setCreatedOn(new Timestamp(date.getTime()));
            pricingMaster.setCreatedBy(userController.getUser());
            pricingMaster.setSubmittedBy(userController.getUser());
            pricingMaster.setSubmittedOn(new Timestamp(date.getTime()));
            pricingMaster.setInternalStatus(intStatusFacade.find(1));
            pricingMaster.setExternalStatus(extStatusFacade.find(1));
            pricingMaster.setVariation(true);
            pricingMaster.setPriceUpdation(price);
            pricingMaster.setRegisterProduct(newProduct);
            pricingMaster.setMinorChange(minorChange);
            em.persist(pricingMaster);

            minorChange.getProductPriceUpdations().add(pricing);

        } catch (Exception e) {
            throw e;
        }
    }
