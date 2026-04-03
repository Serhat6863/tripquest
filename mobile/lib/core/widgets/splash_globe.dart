import 'dart:math';

import 'package:flutter/material.dart';

class SplashGlobe extends StatefulWidget {
  const SplashGlobe({super.key, this.size = 200});

  final double size;

  @override
  State<SplashGlobe> createState() => _SplashGlobeState();
}

class _SplashGlobeState extends State<SplashGlobe>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 8),
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (_, __) => CustomPaint(
        size: Size(widget.size, widget.size),
        painter: _GlobePainter(_controller.value * 2 * pi),
      ),
    );
  }
}

class _GlobePainter extends CustomPainter {
  const _GlobePainter(this.rotation);

  final double rotation;

  ({Offset point, double z}) _projectPoint(
    double lonDeg,
    double latDeg,
    double cx,
    double cy,
    double r,
  ) {
    final lon = lonDeg * pi / 180 + rotation;
    final lat = latDeg * pi / 180;
    final x = cos(lat) * sin(lon);
    final y = sin(lat);
    final z = cos(lat) * cos(lon);
    return (point: Offset(cx + r * x, cy - r * y), z: z);
  }

  List<Offset> _clipToHemisphere(
    List<(double, double)> lonLat,
    double cx,
    double cy,
    double r,
  ) {
    if (lonLat.isEmpty) return [];

    final pts =
        lonLat.map((p) => _projectPoint(p.$1, p.$2, cx, cy, r)).toList();
    final result = <Offset>[];
    final n = pts.length;

    for (int i = 0; i < n; i++) {
      final curr = pts[i];
      final next = pts[(i + 1) % n];

      if (curr.z > 0) {
        result.add(curr.point);
        if (next.z <= 0) {
          final t = curr.z / (curr.z - next.z);
          result.add(Offset(
            curr.point.dx + t * (next.point.dx - curr.point.dx),
            curr.point.dy + t * (next.point.dy - curr.point.dy),
          ));
        }
      } else if (next.z > 0) {
        final t = curr.z / (curr.z - next.z);
        result.add(Offset(
          curr.point.dx + t * (next.point.dx - curr.point.dx),
          curr.point.dy + t * (next.point.dy - curr.point.dy),
        ));
      }
    }

    return result;
  }

  void _drawLandmass(
    Canvas canvas,
    Paint paint,
    List<(double, double)> lonLat,
    double cx,
    double cy,
    double r,
  ) {
    final pts = _clipToHemisphere(lonLat, cx, cy, r);
    if (pts.length < 3) return;
    final path = Path()..moveTo(pts.first.dx, pts.first.dy);
    for (int i = 1; i < pts.length; i++) {
      path.lineTo(pts[i].dx, pts[i].dy);
    }
    path.close();
    canvas.drawPath(path, paint);
  }

  @override
  void paint(Canvas canvas, Size size) {
    final cx = size.width / 2;
    final cy = size.height / 2;
    final r = size.width / 2 * 0.85;

    canvas.drawRect(
      Offset.zero & size,
      Paint()..color = const Color(0xFF0A0E27),
    );

    final rng = Random(42);
    for (int i = 0; i < 60; i++) {
      canvas.drawCircle(
        Offset(
          rng.nextDouble() * size.width,
          rng.nextDouble() * size.height,
        ),
        rng.nextDouble() * 1.2 + 0.3,
        Paint()
          ..color =
              Colors.white.withValues(alpha: 0.4 + rng.nextDouble() * 0.6),
      );
    }

    canvas.drawCircle(
      Offset(cx, cy),
      r,
      Paint()
        ..shader = RadialGradient(
          center: const Alignment(-0.3, -0.3),
          colors: const [
            Color(0xFF2980B9),
            Color(0xFF1B6CA8),
            Color(0xFF0D3B6E),
          ],
          stops: const [0.0, 0.5, 1.0],
        ).createShader(Rect.fromCircle(center: Offset(cx, cy), radius: r)),
    );

    canvas.save();
    canvas.clipPath(
      Path()..addOval(Rect.fromCircle(center: Offset(cx, cy), radius: r)),
    );

    final landPaint = Paint()
      ..color = const Color(0xFF2D8A4E)
      ..style = PaintingStyle.fill;

    for (final continent in _continents) {
      _drawLandmass(canvas, landPaint, continent, cx, cy, r);
    }

    final icePaint = Paint()
      ..color = const Color(0xFFDDEEFF)
      ..style = PaintingStyle.fill;

    _drawLandmass(canvas, icePaint, _antarctica, cx, cy, r);

    canvas.restore();

    for (int i = 0; i < 3; i++) {
      canvas.drawCircle(
        Offset(cx, cy),
        r + 1.5 + i * 2.5,
        Paint()
          ..color = const Color(0xFF6BB8FF)
              .withValues(alpha: 0.07 - i * 0.015)
          ..style = PaintingStyle.stroke
          ..strokeWidth = 2.5 - i * 0.5,
      );
    }

    canvas.drawCircle(
      Offset(cx, cy),
      r,
      Paint()
        ..shader = RadialGradient(
          colors: [
            Colors.transparent,
            Colors.transparent,
            const Color(0xFF88CCFF).withValues(alpha: 0.25),
            const Color(0xFFAADDFF).withValues(alpha: 0.55),
          ],
          stops: const [0.0, 0.72, 0.88, 1.0],
        ).createShader(Rect.fromCircle(center: Offset(cx, cy), radius: r)),
    );

    canvas.drawCircle(
      Offset(cx, cy),
      r,
      Paint()
        ..shader = RadialGradient(
          center: const Alignment(-0.4, -0.45),
          radius: 0.7,
          colors: [
            Colors.white.withValues(alpha: 0.28),
            Colors.white.withValues(alpha: 0.06),
            Colors.transparent,
          ],
          stops: const [0.0, 0.4, 0.75],
        ).createShader(Rect.fromCircle(center: Offset(cx, cy), radius: r)),
    );
  }

  static const List<List<(double, double)>> _continents = [
    _northAmerica,
    _southAmerica,
    _europe,
    _africa,
    _asia,
    _australia,
  ];

  static const List<(double, double)> _northAmerica = [
    (-168.0, 72.0),
    (-140.0, 70.0),
    (-120.0, 68.0),
    (-100.0, 72.0),
    (-85.0, 75.0),
    (-75.0, 72.0),
    (-65.0, 47.0),
    (-55.0, 47.0),
    (-55.0, 42.0),
    (-65.0, 44.0),
    (-70.0, 42.0),
    (-75.0, 35.0),
    (-80.0, 25.0),
    (-85.0, 20.0),
    (-90.0, 15.0),
    (-85.0, 10.0),
    (-77.0, 8.0),
    (-80.0, 0.0),
    (-75.0, 5.0),
    (-70.0, 12.0),
    (-65.0, 18.0),
    (-70.0, 22.0),
    (-75.0, 28.0),
    (-80.0, 30.0),
    (-85.0, 30.0),
    (-90.0, 20.0),
    (-95.0, 19.0),
    (-105.0, 20.0),
    (-110.0, 23.0),
    (-117.0, 32.0),
    (-120.0, 35.0),
    (-124.0, 40.0),
    (-124.0, 48.0),
    (-130.0, 54.0),
    (-140.0, 60.0),
    (-150.0, 61.0),
    (-160.0, 60.0),
    (-165.0, 64.0),
    (-168.0, 66.0),
  ];

  static const List<(double, double)> _southAmerica = [
    (-80.0, 10.0),
    (-75.0, 12.0),
    (-65.0, 12.0),
    (-60.0, 5.0),
    (-50.0, 2.0),
    (-48.0, 0.0),
    (-50.0, -5.0),
    (-45.0, -10.0),
    (-38.0, -12.0),
    (-35.0, -8.0),
    (-35.0, -15.0),
    (-38.0, -20.0),
    (-40.0, -22.0),
    (-45.0, -24.0),
    (-48.0, -28.0),
    (-50.0, -30.0),
    (-52.0, -33.0),
    (-55.0, -35.0),
    (-58.0, -38.0),
    (-62.0, -40.0),
    (-65.0, -45.0),
    (-67.0, -50.0),
    (-68.0, -55.0),
    (-68.0, -58.0),
    (-65.0, -55.0),
    (-62.0, -52.0),
    (-58.0, -52.0),
    (-55.0, -50.0),
    (-50.0, -45.0),
    (-48.0, -28.0),
    (-70.0, -18.0),
    (-75.0, -15.0),
    (-78.0, -8.0),
    (-80.0, -2.0),
    (-80.0, 5.0),
  ];

  static const List<(double, double)> _europe = [
    (-10.0, 36.0),
    (-8.0, 38.0),
    (-8.0, 42.0),
    (-5.0, 44.0),
    (-2.0, 44.0),
    (3.0, 44.0),
    (5.0, 46.0),
    (8.0, 44.0),
    (12.0, 44.0),
    (14.0, 42.0),
    (16.0, 38.0),
    (18.0, 40.0),
    (22.0, 38.0),
    (26.0, 38.0),
    (28.0, 42.0),
    (30.0, 46.0),
    (28.0, 56.0),
    (26.0, 58.0),
    (24.0, 60.0),
    (22.0, 64.0),
    (28.0, 70.0),
    (20.0, 70.0),
    (15.0, 70.0),
    (10.0, 63.0),
    (5.0, 58.0),
    (0.0, 56.0),
    (-2.0, 52.0),
    (-5.0, 48.0),
    (-8.0, 44.0),
    (-10.0, 42.0),
  ];

  static const List<(double, double)> _africa = [
    (-18.0, 16.0),
    (-16.0, 20.0),
    (-17.0, 24.0),
    (-14.0, 28.0),
    (-8.0, 34.0),
    (-5.0, 36.0),
    (0.0, 37.0),
    (5.0, 37.0),
    (10.0, 37.0),
    (12.0, 34.0),
    (14.0, 30.0),
    (18.0, 28.0),
    (22.0, 30.0),
    (28.0, 30.0),
    (32.0, 28.0),
    (36.0, 22.0),
    (42.0, 12.0),
    (44.0, 8.0),
    (42.0, 2.0),
    (40.0, -2.0),
    (38.0, -8.0),
    (36.0, -18.0),
    (35.0, -24.0),
    (32.0, -28.0),
    (28.0, -34.0),
    (24.0, -34.0),
    (20.0, -34.0),
    (18.0, -28.0),
    (16.0, -22.0),
    (14.0, -18.0),
    (12.0, -8.0),
    (8.0, -2.0),
    (2.0, 4.0),
    (0.0, 6.0),
    (-2.0, 5.0),
    (-4.0, 8.0),
    (-8.0, 4.0),
    (-14.0, 10.0),
  ];

  static const List<(double, double)> _asia = [
    (26.0, 42.0),
    (30.0, 46.0),
    (36.0, 42.0),
    (40.0, 38.0),
    (44.0, 40.0),
    (50.0, 42.0),
    (55.0, 42.0),
    (60.0, 44.0),
    (65.0, 42.0),
    (70.0, 38.0),
    (75.0, 36.0),
    (80.0, 32.0),
    (85.0, 28.0),
    (90.0, 22.0),
    (95.0, 18.0),
    (100.0, 14.0),
    (104.0, 10.0),
    (108.0, 12.0),
    (110.0, 18.0),
    (114.0, 22.0),
    (118.0, 24.0),
    (122.0, 28.0),
    (126.0, 30.0),
    (130.0, 34.0),
    (132.0, 38.0),
    (136.0, 36.0),
    (140.0, 38.0),
    (142.0, 42.0),
    (140.0, 46.0),
    (136.0, 50.0),
    (132.0, 48.0),
    (128.0, 48.0),
    (122.0, 52.0),
    (116.0, 54.0),
    (110.0, 54.0),
    (105.0, 52.0),
    (100.0, 56.0),
    (95.0, 56.0),
    (90.0, 58.0),
    (85.0, 60.0),
    (80.0, 62.0),
    (75.0, 64.0),
    (70.0, 68.0),
    (65.0, 70.0),
    (60.0, 72.0),
    (55.0, 72.0),
    (50.0, 70.0),
    (45.0, 68.0),
    (40.0, 66.0),
    (35.0, 62.0),
    (30.0, 58.0),
    (26.0, 54.0),
    (24.0, 50.0),
    (26.0, 46.0),
  ];

  static const List<(double, double)> _australia = [
    (114.0, -22.0),
    (116.0, -20.0),
    (118.0, -18.0),
    (122.0, -18.0),
    (126.0, -14.0),
    (130.0, -12.0),
    (132.0, -12.0),
    (136.0, -12.0),
    (138.0, -14.0),
    (140.0, -18.0),
    (142.0, -22.0),
    (144.0, -24.0),
    (146.0, -26.0),
    (148.0, -28.0),
    (150.0, -30.0),
    (152.0, -32.0),
    (150.0, -36.0),
    (148.0, -38.0),
    (146.0, -38.0),
    (144.0, -36.0),
    (140.0, -36.0),
    (136.0, -34.0),
    (132.0, -32.0),
    (128.0, -32.0),
    (124.0, -30.0),
    (120.0, -28.0),
    (116.0, -26.0),
  ];

  // Polar cap rendered as dense latitude ring + pole so clipping works cleanly
  static const List<(double, double)> _antarctica = [
    (-180.0, -65.0),
    (-160.0, -65.0),
    (-140.0, -65.0),
    (-120.0, -65.0),
    (-100.0, -65.0),
    (-80.0, -65.0),
    (-60.0, -65.0),
    (-40.0, -65.0),
    (-20.0, -65.0),
    (0.0, -65.0),
    (20.0, -65.0),
    (40.0, -65.0),
    (60.0, -65.0),
    (80.0, -65.0),
    (100.0, -65.0),
    (120.0, -65.0),
    (140.0, -65.0),
    (160.0, -65.0),
    (179.9, -65.0),
    (179.9, -90.0),
    (-180.0, -90.0),
  ];

  @override
  bool shouldRepaint(_GlobePainter old) => old.rotation != rotation;
}
