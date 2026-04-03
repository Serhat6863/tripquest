import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/storage/secure_storage.dart';
import '../../../core/widgets/rotating_earth.dart';
import '../../../injection_container.dart';

class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  @override
  void initState() {
    super.initState();
    _navigate();
  }

  Future<void> _navigate() async {
    await Future.delayed(const Duration(seconds: 3));
    if (!mounted) return;
    final hasToken = await sl<SecureStorage>().hasToken();
    if (!mounted) return;
    context.go(hasToken ? '/globe' : '/login');
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      backgroundColor: Color(0xFF0A0E27),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            RotatingEarth(size: 260),
            SizedBox(height: 40),
            Text(
              'TripQuest',
              style: TextStyle(
                color: Colors.white,
                fontSize: 36,
                fontWeight: FontWeight.bold,
                letterSpacing: 2,
              ),
            ),
            SizedBox(height: 8),
            Text(
              'Explore the world',
              style: TextStyle(
                color: Colors.white54,
                fontSize: 16,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
