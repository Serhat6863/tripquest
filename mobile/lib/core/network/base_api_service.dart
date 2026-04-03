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
      final statusCode = e.response?.statusCode;
      return switch (statusCode) {
        401 => ApiError<T>('Unauthorized', 401),
        404 => ApiError<T>('Not found', 404),
        500 => ApiError<T>('Server error', 500),
        _ => ApiError<T>(e.message ?? 'Network error', statusCode),
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
