package ru.geekbrains.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("camera")
public class CameraImpl implements Camera {

    private CameraRoll cameraRoll;

    @Value("false")
    private boolean broken;

    @Autowired
    public CameraImpl(@Qualifier("colorCameraRoll") CameraRoll cameraRoll) {
        this.cameraRoll = cameraRoll;
    }

    @Override
    public CameraRoll getCameraRoll() {
        return cameraRoll;
    }

    @Override
    public void setCameraRoll(CameraRoll cameraRoll) {
        this.cameraRoll = cameraRoll;
    }

    @Override
    public void doPhotograph() {
        if (isBroken()) {
            System.out.println("Фотоаппарат сломан!");
            return;
        }
        cameraRoll.processing();
        System.out.println("Сделана фотография");
    }

    @Override
    public void breaking() {
        this.broken = true;
    }

    @Override
    public boolean isBroken() {
        return broken;
    }

    @Override
    public void ready() {
        System.out.println("Фотоаппарат готов к использованию");
    }
}
