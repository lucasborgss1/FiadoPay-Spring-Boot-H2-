package edu.ucsal.fiadopay.controller;
public record TokenResponse(String access_token, String token_type, long expires_in) {}
