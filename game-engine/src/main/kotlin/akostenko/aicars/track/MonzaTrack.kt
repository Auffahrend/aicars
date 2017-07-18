package akostenko.aicars.track

class MonzaTrack : Track() {
    override val width = 10.0 // m

    override val sections = TrackBuilder.start(0.0, 0.0, 0.0, width)
            .straight(940.0)
            // Retiffilo
            .right(12.0, 80.0).straight(22.0).left(11.0, 100.0)
            .straight(105.0)
            .right(82.0, 20.0)
            .straight(169.0)
            // Curva Grande
            .right(235.5, 45.0).right(320.0, 35.0)
            .straight(432.1)
            // della Roggia
            .left(16.0, 70.0).straight(28.0).right(10.0, 55.0)
            .straight(300.0)
            // Lesmo 1
            .right(55.0, 80.0).right(60.0, 30.0)
            .straight(250.0)
            // Lesmo 2
            .right(30.0, 30.0).right(35.0, 30.0)
            .straight(366.0)
            .left(215.0, 10.0)
            .straight(600.0)
            // Ascari
            .left(35.0, 55.0).straight(45.0).right(115.0, 50.0).left(40.0, 20.0).left(71.0, 20.0)
            .straight(1001.0)
            // Parabolica
            .right(70.0, 90.0).right(130.0, 55.0).right(340.0, 25.0).right(1461.0, 10.0)
            .straight(53.0-8.81)
            .done()

    override val title: String = NAME

    companion object {

        internal val NAME = "Monza"
    }
}
