package id.pdam.billing.application.dto.response;

public record NotifikasiResponse(String id, String type, String title, String body, String time, String group, boolean unread) {}
