package com.example.data

data class SupplyProduct(
    val id: String,
    val name: String,
    val category: String, // "Soil Prep", "Containers", "Pest Control", "Seeds"
    val description: String,
    val priceEstimate: String,
    val productUrl: String,
    val importance: String
)

data class SowingGuide(
    val cropName: String,
    val icon: String,
    val category: String,
    val bestZone: String,
    val sowingTime: String,
    val harvestingTime: String,
    val soilHint: String,
    val depthHint: String,
    val waterNeed: String, // "Low", "Medium", "High"
    val fertilType: String,
    val daysToHarvest: Int
)

data class PestTip(
    val pestName: String,
    val description: String,
    val organicRemedy: String,
    val remedyRecipe: String,
    val preventiveMeasure: String
)

object GardeningGuides {
    val SOIL_PREPARATION_STAGES = listOf(
        "1. Load Constraints Check" to "Rooftops/terraces have load limits. Standard garden soil is extremely heavy when wet (~1.6g/cm³). Use lightweight mediums like cocopeat or perlite.",
        "2. The Perfect Urban Mix" to "Mix 30% Coconut Coir/Cocopeat (for moisture retention), 35% Vermicompost or Aged manure (for nutrients), 20% Pumice/Perlite (for drainage/aeration), and 15% local garden soil with a handful of Neem Cake powder (to block root pathogens).",
        "3. Container Setup" to "Select lightweight, highly breathable fabric grow bags or UV-stabilized plastic pots. Ensure active drainage holes to prevent root rot.",
        "4. Organic Initialization" to "Fill the containers, water them thoroughly with water mixed with a pinch of seaweed extract or Epsom salt, and let the soil rest for 5–7 days before planting seeds or transplanting seedlings to establish microbial activity."
    )

    val ORGANIC_PEST_GUIDES = listOf(
        PestTip(
            pestName = "Aphids & Whiteflies",
            description = "Tiny soft-bodied sucking insects clustering on the undersides of tender terrace leaves.",
            organicRemedy = "Home Neem Oil Emulsion",
            remedyRecipe = "Mix 5ml pure cold-pressed Neem Oil with 2-3 drops of organic liquid soap in 1 liter of warm water. Shake vigorously and spray under the leaves in the cool evening.",
            preventiveMeasure = "Install Yellow Sticky Cards and plant marigolds nearby acting as trap crops."
        ),
        PestTip(
            pestName = "Spider Mites",
            description = "Minute, spider-like mites occurring during dry, hot terrace days, creating fine web sheets on foliage.",
            organicRemedy = "High-Pressure Water Rinse & Rosemary spray",
            remedyRecipe = "Spray plants with a firm stream of cold water to dislodge mites, then follow up by misting with Rosemary oil dillution (3ml oil per liter).",
            preventiveMeasure = "Maintain high relative humidity on the terrace by misting of surrounding concrete spaces."
        ),
        PestTip(
            pestName = "Caterpillars & Leaf Miners",
            description = "Larvae chewing random visual holes or mining translucent white squiggly trails inside leaves.",
            organicRemedy = "Handpicking & Bacillus thuringiensis (Bt) spray",
            remedyRecipe = "Pick large caterpillars off by hand at night. For miners, pinch infested leaves and spray Bt organic wash (1g/L) during active flights.",
            preventiveMeasure = "Cover new seedlings with light pest-barrier insect nets during early seasonal vegetative growth."
        ),
        PestTip(
            pestName = "Root Pathogens / Damping Off",
            description = "Fungal infection causing seedlings to suddenly collapse at soil levels.",
            organicRemedy = "Cinnamon powder & Neem cake wash",
            remedyRecipe = "Dust fine ground organic cinnamon powder lightly on the soil surface. Watering with diluted compost tea also inoculates positive soil microbes.",
            preventiveMeasure = "Avoid waterlogging. Ensure cocopeat or vermiculite is well solarized before sowing sensitive seeds."
        )
    )

    val SOWING_GUIDES = listOf(
        SowingGuide(
            cropName = "Cherry Tomatoes",
            icon = "🍅",
            category = "Vegetables",
            bestZone = "Zone 5 - 9 (All Zones)",
            sowingTime = "Spring (Feb-Apr) indoors / Autumn in hot zones",
            harvestingTime = "Summer to late Autumn (June-Oct)",
            soilHint = "Rich, loamy, well-aerated with high organic potassium compost.",
            depthHint = "0.5 cm (Seedbeds for transplanting afterward)",
            waterNeed = "Medium - Keep soil consistently damp, never soggy.",
            fertilType = "Liquid seaweed monthly, calcium-rich eggshell dust.",
            daysToHarvest = 75
        ),
        SowingGuide(
            cropName = "Lush Spinach (Palak)",
            icon = "🥬",
            category = "Leafy Greens",
            bestZone = "Zone 4 - 10 (Cooler preference)",
            sowingTime = "Early Spring, Autumn to late Winter",
            harvestingTime = "Harvest baby leaves starting around 30 days onwards",
            soilHint = "Nitrogen-rich compost, high moisture-holding cocopeat.",
            depthHint = "1 cm directly sown in shallow wide grow bags",
            waterNeed = "High - Leafy greens require constant ambient moisture.",
            fertilType = "Nitrogen-boosting vermicompost tea every 10 days.",
            daysToHarvest = 40
        ),
        SowingGuide(
            cropName = "Spicy Chilli Peppers",
            icon = "🌶️",
            category = "Vegetables",
            bestZone = "Zone 7 - 11 (Warm climates)",
            sowingTime = "Spring (Mar-May) under bright warm sunlight",
            harvestingTime = "Late summer to winter",
            soilHint = "Well-drained sand-manure mix, warm soil temperatures.",
            depthHint = "0.5 cm (Seedbeds, needs 25°C+ to germinate)",
            waterNeed = "Low - Let topsoil dry out slightly between waterings.",
            fertilType = "Balanced bone meal, wood ash for phosphorus/potassium.",
            daysToHarvest = 90
        ),
        SowingGuide(
            cropName = "Italian Red Basil",
            icon = "🌿",
            category = "Herbs",
            bestZone = "Zone 6 - 11",
            sowingTime = "Spring / early Summer, requires high light hours",
            harvestingTime = "Pinch tops continuously once 15 cm high.",
            soilHint = "Fluffy cocopeat compost mix with high aeration.",
            depthHint = "0.3 cm directly in pots",
            waterNeed = "Medium - Water deep when the top container drys.",
            fertilType = "Light compost dressing every 14 days.",
            daysToHarvest = 55
        ),
        SowingGuide(
            cropName = "French Radish",
            icon = "🥕",
            category = "Roots",
            bestZone = "Zone 3 - 9 (Cool seasons)",
            sowingTime = "Late Autumn to early Spring, cool moist air",
            harvestingTime = "Within 4 weeks of rapid root development",
            soilHint = "Extremely loose sand-compost, zero pebbles (causes split roots).",
            depthHint = "1 cm deep in rows",
            waterNeed = "High - Water steadily to keep roots crisp and mild.",
            fertilType = "Phosphorus-rich organic manures, skip high nitrogen.",
            daysToHarvest = 30
        )
    )

    val AFFILIATE_PRODUCTS = listOf(
        SupplyProduct(
            id = "prod_cocopeat",
            name = "Premium Low-EC Cocopeat Block (5kg)",
            category = "Soil Prep",
            description = "Compacted pure coconut coir dust, thoroughly washed to lower salt indices. Expands to 75 liters of rich growing medium when water is added.",
            priceEstimate = "$14.99",
            productUrl = "https://example.com/affiliate/cocopeat-block",
            importance = "Highly Recommended"
        ),
        SupplyProduct(
            id = "prod_vermicompost",
            name = "Aero-Engineered Organic Vermicompost (10 lbs)",
            category = "Soil Prep",
            description = "Odorless earthworm castings loaded with rich beneficial microbes. Provides slow-release natural nutrition for high terrace yields.",
            priceEstimate = "$18.50",
            productUrl = "https://example.com/affiliate/vermicompost-castings",
            importance = "Essential Nutrition"
        ),
        SupplyProduct(
            id = "prod_neem_cake",
            name = "Dual Action Neem Cake Powder (5 lbs)",
            category = "Pest Control",
            description = "Rich organic fertiliser with built-in systemic insect prevention. Safe, biodegradable, controls root nematodes and soil fungus.",
            priceEstimate = "$12.99",
            productUrl = "https://example.com/affiliate/neem-cake-powder",
            importance = "Pest Preventive"
        ),
        SupplyProduct(
            id = "prod_grow_bags",
            name = "Heavy Duty Fabric Grow Bags (5 Pack - 5 Gal)",
            category = "Containers",
            description = "Breathable non-woven fabric bags that air-prune plant roots, preventing root circling. Extremely lightweight, perfect for balcony loads.",
            priceEstimate = "$15.99",
            productUrl = "https://example.com/affiliate/fabric-grow-bags",
            importance = "Rooftops Favorite"
        ),
        SupplyProduct(
            id = "prod_spray_bottle",
            name = "Ergonomic Hand-Pump Pressure Sprayer (1.5L)",
            category = "Pest Control",
            description = "Continuous adjustable mist sprayer. Extremely helpful for applying organic neem oil washes or watering delicate seeds thoroughly.",
            priceEstimate = "$11.45",
            productUrl = "https://example.com/affiliate/pressure-sprayer",
            importance = "Highly Helpful"
        )
    )
}
