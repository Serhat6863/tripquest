import 'package:flutter/material.dart';

class RotatingEarth extends StatelessWidget {
  const RotatingEarth({super.key, this.size = 200});

  final double size;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF1B6CA8).withValues(alpha: 0.6),
            blurRadius: size * 0.15,
            spreadRadius: size * 0.05,
          ),
        ],
      ),
      child: ClipOval(
        child: Image.asset(
          'assets/images/earth.png',
          width: size,
          height: size,
          fit: BoxFit.cover,
        ),
      ),
    );
  }
}
