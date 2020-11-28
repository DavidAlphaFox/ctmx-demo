(ns htmx.persistence.cv)

(def areasOfWork
  (sort
    ["Inbound and Outbound Investment",
     "Regulatory & compliance",
     "Capital Markets",
     "Mergers & acquisitions",
     "Joint ventures",
     "Commercial Contracting",
     "Corporate",
     "Litigations & disputes",
     "General counsel (local, regional, or global)",
     "Restructuring & insolvency",
     "Company secretarial",
     "Corporate governance",
     "Employment & Labour",
     "Tax",
     "Intellectual property – patents",
     "Intellectual property – copyrights & trademarks",
     "Competition and Antitrust",
     "Real estate"]))

(def industries
  (sort
    ["Retail & commercial banking",
     "Investment banking and finance",
     "Payments",
     "Venture capital",
     "Private equity",
     "Funds",
     "Asset management",
     "Pharmaceuticals & medical services",
     "Technology",
     "E-commerce",
     "Government",
     "Construction & infrastructure",
     "Transportation",
     "Start-ups",
     "Telecommunications",
     "Media",
     "Retail, apparel & luxury",
     "Real estate",
     "Oil & gas",
     "Logistics",
     "Aviation",
     "Consumer goods",
     "FMCG",
     "Tobacco & alternatives",
     "Food & beverage",
     "Fintech",
     "Entertainment & Leisure",
     "Hospitality",
     "Insurance & reinsurance"]))

(def jurisdictions
  ["United Kingdom (UK)",
   "Hong Kong (HK)",
   "Singapore",
   "USA",
   "People’s Republic of China (PRC)",
   "Malaysia",
   "Australia",
   "New Zealand",
   "France",
   "Other"])

(def locations
  ["Australia",
   "Canada",
   "China (People's Republic of China)",
   "France",
   "Germany",
   "Hong Kong",
   "India",
   "Indonesia",
   "Malaysia",
   "New Zealand",
   "Singapore",
   "Switzerland",
   "UK",
   "US",
   "Vietnam"])

(def expenseRatings
  ["Paralegal",
   "Legal Consultant",
   "Senior Legal Consultant",
   "Managing Consultant",
   "Legal Director"])

(def cv
  (atom
    {:country "",
     :countryOther "",
     :summary "",
     :areasOfWork [],
     :industries [],
     :areasOfWorkTop3 [],
     :industryTop3 [],
     :areasOfWorkFuture [],
     :industryFuture [],
     :previousLegalRoles [],
     :previousRoles [],
     :admissions [],
     :qualifications [],
     :languages [],
     :interests ""}))

