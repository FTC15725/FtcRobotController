package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class shooterSubsystem {
    private DcMotor intakeMotor;
    private DcMotorEx flywheelMotor;
    private DcMotor indexerMotor; // moves balls from intake to flywheel

    // PIDF velocity tuning parameters for flywheel
    public static double P = 10.0;
    public static double I = 3.0;
    public static double D = 0.0;
    public static double F = 12.0;

    // Maximum target velocity in encoder ticks per second
    public static double MAX_VELOCITY = 2800.0;

    public void init(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheelMotor");
        indexerMotor = hardwareMap.get(DcMotor.class, "indexerMotor");

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        indexerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(P, I, D, F));
    }

    public void setIntakePower(double power) {
        if (intakeMotor != null) {
            intakeMotor.setPower(power);
        }
    }

    /**
     * Set target power for the flywheel using closed-loop PIDF velocity control.
     * @param power Power ratio from -1.0 to 1.0 (mapped to max velocity in ticks/sec).
     */
    public void setFlywheelPower(double power) {
        if (flywheelMotor != null) {
            if (power == 0.0) {
                flywheelMotor.setPower(0.0);
            } else {
                flywheelMotor.setVelocity(power * MAX_VELOCITY);
            }
        }
    }

    /**
     * Directly set target flywheel velocity in encoder ticks per second using PIDF control.
     * @param ticksPerSecond Target velocity in encoder ticks/sec.
     */
    public void setFlywheelVelocity(double ticksPerSecond) {
        if (flywheelMotor != null) {
            flywheelMotor.setVelocity(ticksPerSecond);
        }
    }

    /**
     * Update the PIDF coefficients for flywheel velocity control.
     */
    public void setFlywheelPIDF(double p, double i, double d, double f) {
        P = p;
        I = i;
        D = d;
        F = f;
        if (flywheelMotor != null) {
            flywheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
        }
    }

    /**
     * Get current flywheel velocity in ticks per second.
     */
    public double getFlywheelVelocity() {
        if (flywheelMotor != null) {
            return flywheelMotor.getVelocity();
        }
        return 0.0;
    }

    public void setIndexerPower(double power) {
        if (indexerMotor != null) {
            indexerMotor.setPower(power);
        }
    }

    public void stopAll() {
        setIntakePower(0);
        setFlywheelPower(0);
        setIndexerPower(0);
    }

    /**
     * Optional Software PIDF Controller helper for custom calculation loops.
     */
    public static class PIDFController {
        public double kP, kI, kD, kF;
        private double integralSum = 0;
        private double lastError = 0;
        private double lastTime = 0;

        public PIDFController(double kP, double kI, double kD, double kF) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = kF;
        }

        public double calculate(double current, double target) {
            double error = target - current;
            double currentTime = System.currentTimeMillis() / 1000.0;
            double dt = (lastTime == 0) ? 0.02 : (currentTime - lastTime);

            integralSum += error * dt;
            double derivative = dt > 0 ? (error - lastError) / dt : 0;

            lastError = error;
            lastTime = currentTime;

            return (kP * error) + (kI * integralSum) + (kD * derivative) + (kF * target);
        }

        public void reset() {
            integralSum = 0;
            lastError = 0;
            lastTime = 0;
        }
    }
}
