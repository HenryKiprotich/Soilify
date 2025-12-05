# Quick Fix Guide for Other Activities 🚀

If you have other activities that need to make authenticated API calls, follow this pattern:

## ❌ OLD WAY (Won't Work):
```java
// This doesn't include authentication token!
SomeApi api = ApiClient.getClient().create(SomeApi.class);
```

## ✅ NEW WAY (Correct):
```java
// This automatically includes the auth token
SomeApi api = ApiClient.getClient(this).create(SomeApi.class);
```

---

## Activities to Update

### 1. **FieldsActivity** (or similar)
```java
private void loadFields() {
    FieldsApi api = ApiClient.getClient(this).create(FieldsApi.class);
    Call<List<Field>> call = api.getFields();
    call.enqueue(new Callback<List<Field>>() {
        @Override
        public void onResponse(Call<List<Field>> call, Response<List<Field>> response) {
            if (response.isSuccessful()) {
                // Handle success
            } else if (response.code() == 401) {
                redirectToLogin();
            }
        }
        
        @Override
        public void onFailure(Call<List<Field>> call, Throwable t) {
            // Handle error
        }
    });
}

private void redirectToLogin() {
    getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

### 2. **AlertsActivity**
```java
private void loadAlerts() {
    AlertsApi api = ApiClient.getClient(this).create(AlertsApi.class);
    // ... rest of code
}
```

### 3. **AnalyticsActivity**
```java
private void loadAnalytics() {
    AnalyticsApi api = ApiClient.getClient(this).create(AnalyticsApi.class);
    // ... rest of code
}
```

### 4. **RecordFertiliserActivity**
```java
private void submitFertilizer() {
    FertilizerApi api = ApiClient.getClient(this).create(FertilizerApi.class);
    // ... rest of code
}
```

### 5. **RecommendationsActivity**
```java
private void loadRecommendations() {
    AIApi api = ApiClient.getClient(this).create(AIApi.class);
    // ... rest of code
}
```

---

## Pattern Summary

### For ANY activity making authenticated API calls:

1. **Replace this:**
   ```java
   ApiClient.getClient().create(YourApi.class)
   ```

2. **With this:**
   ```java
   ApiClient.getClient(this).create(YourApi.class)
   ```

3. **Add 401 handling in onResponse:**
   ```java
   if (response.code() == 401) {
       redirectToLogin();
   }
   ```

4. **Add redirectToLogin() method:**
   ```java
   private void redirectToLogin() {
       getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
       Intent intent = new Intent(this, LoginActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
       finish();
   }
   ```

---

## Exception: Login & Signup

**ONLY** `LoginActivity` and `SignUpActivity` should use:
```java
ApiClient.getClientNoAuth().create(AuthApi.class)
```

Because they don't have a token yet!

---

## How It Works Behind the Scenes

When you call `ApiClient.getClient(this)`:
1. Creates OkHttpClient with `AuthInterceptor`
2. `AuthInterceptor` reads token from SharedPreferences
3. Adds `Authorization: Bearer <token>` to every request
4. Your API calls work automatically! ✨

---

## Search & Replace Guide

Use Find & Replace in Android Studio:

**Find:** `ApiClient.getClient().create`
**Replace:** `ApiClient.getClient(this).create`

**Exclude:** `LoginActivity.java` and `SignUpActivity.java`

Then manually review each change to ensure it's in an Activity context.

---

That's it! Your authentication is now fully functional! 🎉
