# Analytics API Documentation

## Overview
Comprehensive analytics endpoints for the Soilify farming management system, providing insights into fertilizer usage, weather data, field performance, and overall farm metrics.

## Base URL
`/api/analytics`

## Authentication
All endpoints require JWT authentication via HttpOnly cookie (farmer_id extracted automatically).

---

## Endpoints

### 1. Farm Overview
**GET** `/api/analytics/overview`

Get a complete snapshot of the farm with all key metrics.

**Response:**
```json
{
  "total_fields": 5,
  "total_area_hectares": 25.5,
  "fertilizer_summary": {
    "total_applications": 42,
    "total_amount_kg": 850.0,
    "average_amount_per_application": 20.24,
    "most_used_fertilizer": "NPK 15-15-15",
    "fields_fertilized": 5
  },
  "weather_summary": {
    "total_records": 128,
    "average_temperature": 24.5,
    "average_rainfall": 45.2,
    "average_soil_moisture": 68.3,
    "max_temperature": 35.2,
    "max_rainfall": 120.5,
    "min_temperature": 15.8,
    "fields_monitored": 5
  },
  "total_alerts": 12,
  "alerts_this_week": 3,
  "alerts_today": 1,
  "last_fertilizer_application": "2024-12-03",
  "last_weather_record": "2024-12-04T08:30:00",
  "last_alert": "2024-12-04T10:15:00"
}
```

---

### 2. Fertilizer Usage by Type
**GET** `/api/analytics/fertilizer/by-type`

Get fertilizer usage breakdown by fertilizer type.

**Response:**
```json
[
  {
    "fertiliser_type": "NPK 15-15-15",
    "total_amount_kg": 450.0,
    "application_count": 25,
    "percentage_of_total": 52.94
  },
  {
    "fertiliser_type": "Urea",
    "total_amount_kg": 280.0,
    "application_count": 12,
    "percentage_of_total": 32.94
  }
]
```

---

### 3. Fertilizer Usage by Field
**GET** `/api/analytics/fertilizer/by-field`

Get fertilizer usage breakdown by field.

**Response:**
```json
[
  {
    "field_id": 1,
    "field_name": "North Field",
    "total_amount_kg": 320.0,
    "application_count": 15,
    "most_used_type": "NPK 15-15-15"
  }
]
```

---

### 4. Fertilizer Usage by Month
**GET** `/api/analytics/fertilizer/by-month?months=12`

Get monthly fertilizer usage trends.

**Query Parameters:**
- `months` (optional): Number of months to include (1-24, default: 12)

**Response:**
```json
[
  {
    "month": "December",
    "year": 2024,
    "total_amount_kg": 125.5,
    "application_count": 8
  },
  {
    "month": "November",
    "year": 2024,
    "total_amount_kg": 98.3,
    "application_count": 6
  }
]
```

---

### 5. Weather Data by Field
**GET** `/api/analytics/weather/by-field`

Get weather statistics grouped by field.

**Response:**
```json
[
  {
    "field_id": 1,
    "field_name": "North Field",
    "average_temperature": 24.5,
    "average_rainfall": 45.2,
    "average_soil_moisture": 68.3,
    "record_count": 35
  }
]
```

---

### 6. Weather Trends
**GET** `/api/analytics/weather/trends?days=30&field_id=1`

Get daily weather trends over time.

**Query Parameters:**
- `days` (optional): Number of days to include (7-90, default: 30)
- `field_id` (optional): Filter by specific field

**Response:**
```json
[
  {
    "date": "2024-12-04",
    "average_temperature": 26.5,
    "average_rainfall": 12.3,
    "average_soil_moisture": 72.1
  }
]
```

---

### 7. Field Analytics
**GET** `/api/analytics/field/{field_id}`

Get comprehensive analytics for a specific field.

**Response:**
```json
{
  "field_id": 1,
  "field_name": "North Field",
  "soil_type": "Clay Loam",
  "crop_type": "Corn",
  "size_hectares": 5.5,
  "total_fertilizer_kg": 320.0,
  "fertilizer_applications": 15,
  "last_fertilizer_date": "2024-12-03",
  "weather_records": 35,
  "avg_temperature": 24.5,
  "avg_rainfall": 45.2,
  "avg_soil_moisture": 68.3,
  "total_alerts": 4,
  "recent_alerts": 1
}
```

---

### 8. Date Range Statistics
**GET** `/api/analytics/date-range?start_date=2024-11-01&end_date=2024-11-30`

Get statistics for a custom date range.

**Query Parameters:**
- `start_date` (required): Start date (YYYY-MM-DD)
- `end_date` (required): End date (YYYY-MM-DD)

**Response:**
```json
{
  "start_date": "2024-11-01",
  "end_date": "2024-11-30",
  "fertilizer_applications": 12,
  "total_fertilizer_kg": 240.5,
  "weather_records": 28,
  "avg_temperature": 23.8,
  "avg_rainfall": 38.5,
  "alerts_count": 3
}
```

---

### 9. Top Fields
**GET** `/api/analytics/top-fields?limit=5`

Get top performing fields by various metrics.

**Query Parameters:**
- `limit` (optional): Number of fields to return (1-10, default: 5)

**Response:**
```json
{
  "most_fertilized": [
    {
      "field_id": 1,
      "field_name": "North Field",
      "total_amount_kg": 320.0,
      "application_count": 15,
      "most_used_type": "NPK 15-15-15"
    }
  ],
  "largest_fields": [
    {
      "field_id": 2,
      "field_name": "South Field",
      "size_hectares": 8.5,
      "crop_type": "Wheat"
    }
  ],
  "most_alerts": [
    {
      "field_id": 3,
      "field_name": "East Field",
      "alert_count": 6
    }
  ]
}
```

---

### 10. Efficiency Metrics
**GET** `/api/analytics/efficiency-metrics`

Calculate fertilizer efficiency and get recommendations.

**Response:**
```json
{
  "total_area_hectares": 25.5,
  "total_fertilizer_kg": 850.0,
  "fertilizer_per_hectare": 33.33,
  "fields_analyzed": 5,
  "recommendation": "Low fertilizer usage. Consider increasing application for better yields."
}
```

**Recommendation Ranges:**
- `< 50 kg/ha`: Low usage - consider increasing
- `50-150 kg/ha`: Good usage - optimal range
- `150-250 kg/ha`: Moderate to high - ensure proper timing
- `> 250 kg/ha`: High usage - consider soil testing

---

### 11. Analytics by Crop Type
**GET** `/api/analytics/by-crop-type`

Get analytics grouped by crop type.

**Response:**
```json
[
  {
    "crop_type": "Corn",
    "fields_count": 3,
    "total_area_hectares": 15.5,
    "total_fertilizer_kg": 520.0,
    "avg_fertilizer_per_hectare": 33.55,
    "avg_temperature": 24.8,
    "avg_rainfall": 48.2
  }
]
```

---

### 12. Analytics by Soil Type
**GET** `/api/analytics/by-soil-type`

Get analytics grouped by soil type.

**Response:**
```json
[
  {
    "soil_type": "Clay Loam",
    "fields_count": 3,
    "total_area_hectares": 14.5,
    "total_fertilizer_kg": 480.0,
    "avg_fertilizer_per_hectare": 33.10,
    "most_common_crop": "Corn"
  }
]
```

---

## Use Cases for Android App

### Dashboard Screen
```kotlin
// Load farm overview
val overview = apiService.getFarmOverview()
displayTotalFields(overview.total_fields)
displayFertilizerSummary(overview.fertilizer_summary)
displayWeatherSummary(overview.weather_summary)
displayAlerts(overview.total_alerts, overview.alerts_today)
```

### Fertilizer Analytics Screen
```kotlin
// Show fertilizer breakdown
val byType = apiService.getFertilizerByType()
displayPieChart(byType) // Show distribution by type

val byField = apiService.getFertilizerByField()
displayBarChart(byField) // Show usage per field

val byMonth = apiService.getFertilizerByMonth(12)
displayLineChart(byMonth) // Show monthly trends
```

### Weather Analytics Screen
```kotlin
// Show weather trends
val trends = apiService.getWeatherTrends(days = 30)
displayWeatherChart(trends)

val byField = apiService.getWeatherByField()
displayFieldWeatherComparison(byField)
```

### Field Detail Screen
```kotlin
// Show comprehensive field analytics
val analytics = apiService.getFieldAnalytics(fieldId)
displayFieldOverview(analytics)
displayFertilizerHistory(analytics)
displayWeatherStats(analytics)
displayAlertCount(analytics)
```

### Reports Screen
```kotlin
// Generate custom reports
val dateRange = apiService.getDateRangeStats(
    startDate = "2024-11-01",
    endDate = "2024-11-30"
)
displayMonthlyReport(dateRange)

val efficiency = apiService.getEfficiencyMetrics()
displayRecommendation(efficiency.recommendation)
```

---

## Android Integration Example

```kotlin
// Retrofit Interface
interface AnalyticsApi {
    @GET("api/analytics/overview")
    suspend fun getFarmOverview(): Response<FarmOverview>
    
    @GET("api/analytics/fertilizer/by-type")
    suspend fun getFertilizerByType(): Response<List<FertilizerByType>>
    
    @GET("api/analytics/weather/trends")
    suspend fun getWeatherTrends(
        @Query("days") days: Int = 30,
        @Query("field_id") fieldId: Int? = null
    ): Response<List<WeatherTrend>>
    
    @GET("api/analytics/field/{field_id}")
    suspend fun getFieldAnalytics(
        @Path("field_id") fieldId: Int
    ): Response<FieldAnalytics>
}

// Usage in ViewModel
class DashboardViewModel(private val api: AnalyticsApi) : ViewModel() {
    private val _overview = MutableLiveData<FarmOverview>()
    val overview: LiveData<FarmOverview> = _overview
    
    fun loadDashboard() {
        viewModelScope.launch {
            try {
                val response = api.getFarmOverview()
                if (response.isSuccessful) {
                    _overview.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
```

---

## Benefits

1. **Data-Driven Decisions**: Farmers can make informed decisions based on historical data
2. **Performance Tracking**: Monitor field performance over time
3. **Resource Optimization**: Identify over/under-fertilized fields
4. **Weather Insights**: Understand weather patterns and their impact
5. **Efficiency Metrics**: Get personalized recommendations for improvement
6. **Comparative Analysis**: Compare different fields, crops, and soil types

---

## Notes

- All endpoints automatically filter by authenticated farmer (from JWT token)
- Date ranges use ISO 8601 format (YYYY-MM-DD)
- Decimal values are returned as floats for easy chart rendering
- Null values indicate no data available (e.g., no weather records)
- Percentage calculations handle division by zero gracefully
