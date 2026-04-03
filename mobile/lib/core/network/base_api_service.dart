import 'dart:io';
import 'package:dio/dio.dart';
import 'api_response.dart';

abstract class BaseApiService {
  BaseApiService(this._dio);

  final Dio _dio;

  Future<ApiResponse<T>> request<T>({
    required String method,
    required String path,
    Map<String, dynamic>? data,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.request<dynamic>(
        path,
        data: data,
        options: Options(method: method),
      );
      return ApiSuccess<T>(fromJson!(response.data));
    } on DioException catch (e) {
      if (e.error is SocketException) {
        return ApiError<T>('No internet connection', null);
      }
      return switch (e.type) {
        DioExceptionType.connectionTimeout =>
          ApiError<T>('Connection timeout, try again', null),
        DioExceptionType.receiveTimeout =>
          ApiError<T>('Server is slow, try again', null),
        DioExceptionType.connectionError =>
          ApiError<T>('Cannot reach server', null),
        _ => switch (e.response?.statusCode) {
            400 => ApiError<T>('Invalid credentials', 400),
            401 => ApiError<T>('Wrong email or password', 401),
            404 => ApiError<T>('Not found', 404),
            409 => ApiError<T>('Email already exists', 409),
            500 => ApiError<T>('Server error, try again later', 500),
            _ => ApiError<T>(
                e.message ?? 'Network error', e.response?.statusCode),
          },
      };
    } catch (_) {
      return ApiError<T>('Unexpected error', null);
    }
  }

  Future<ApiResponse<T>> get<T>(
    String path, {
    T Function(dynamic)? fromJson,
  }) =>
      request<T>(method: 'GET', path: path, fromJson: fromJson);

  Future<ApiResponse<T>> post<T>(
    String path, {
    Map<String, dynamic>? data,
    T Function(dynamic)? fromJson,
  }) =>
      request<T>(method: 'POST', path: path, data: data, fromJson: fromJson);

  Future<ApiResponse<T>> put<T>(
    String path, {
    Map<String, dynamic>? data,
    T Function(dynamic)? fromJson,
  }) =>
      request<T>(method: 'PUT', path: path, data: data, fromJson: fromJson);

  Future<ApiResponse<T>> delete<T>(String path) =>
      request<T>(method: 'DELETE', path: path);
}
