class AuthToken {
  const AuthToken({required this.token});

  final String token;

  factory AuthToken.fromJson(Map<String, dynamic> json) =>
      AuthToken(token: json['token'] as String);

  Map<String, dynamic> toJson() => {'token': token};
}
