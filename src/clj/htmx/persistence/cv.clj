(ns htmx.persistence.cv
  (:require
    [htmx.util :as util]))

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

(def cv-raw
 {:country "",
  :countryOther "",
  :summary "",
  :areasOfWork [],
  :industries [],
  :areasOfWorkTop3 [],
  :industryTop3 [],
  :areasOfWorkFuture [],
  :industryFuture [],
  :previousLegalRoles
  [{:title "a",
    :company "b",
    :period-selector
    {:from-month "Not Specified",
     :from-year "1960",
     :to-row {:present "on"}},
    :location "a",
    :multiple-subroles "false",
    :subroles {:details "b"}}
   {:title "c",
    :company "d",
    :period-selector
    {:from-month "Not Specified",
     :from-year "1960",
     :to-row {:present "on"}},
    :location "a",
    :multiple-subroles "false",
    :subroles {:details "b"}}],
  :previousRoles [],
  :admissions [],
  :qualifications [],
  :languages [],
  :interests ""})

(def cv (atom cv-raw))
(defn cv-reset! []
 (reset! cv cv-raw))

(defn add-legal-role [role]
  (swap! cv update :previousLegalRoles conj role))
(defn insert-legal-role [i role]
  (swap! cv assoc-in [:previousLegalRoles i] role))
(defn remove-legal-role [i]
  (swap! cv update :previousLegalRoles util/dissoc-i i))
