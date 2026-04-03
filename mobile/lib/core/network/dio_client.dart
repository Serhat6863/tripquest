import 'package:dio/dio.dart';
import '../constants/api_constants.dart';
import '../constants/app_constants.dart';

class DioClient {
  DioClient() {
    _dio = Dio(
      BaseOptions(
        baseUrl: ApiConstants.baseUrl,
        connectTimeout:
            const Duration(milliseconds: AppConstants.connectTimeoutMs),
        receiveTimeout:
            const Duration(milliseconds: AppConstants.receiveTimeoutMs),
        headers: {'Content-Type': 'application/json'},
      ),
    );
  }

  late final Dio _dio;

  Dio get dio => _dio;
}
