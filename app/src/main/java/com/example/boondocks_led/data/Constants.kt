package com.example.boondocks_led.data

object Constants {
    const val ANTARCTICA = "antarctica"


    //serialization keys
    //Lights
    const val LED_SCENE = "LEDScene" // values 1, 2, or 3
    const val LIGHTBAR = "Lightbar" // value of "On" or "Off" todo: maybe push for boolean or int
    const val DRIVER_LIGHTS = "DrvrLights" // values of "On" or "Off" see above todo
    const val PASSENGER_LIGHTS = "PassLights" // values of "On" or "Off" see above todo
    const val WORK_LIGHTS = "WorkLights" // values of "On" or "Off" see above todo

    //Motors
    const val GET_SEQ_NAMES = "SEQNames" // value 0, used to GET list of sequence names
    const val MOTOR_SEQ = "MotorSeq" // send with values 1-8 to set motor sequence
    const val SEQ1 = "SEQ1" // each of these keys is returned with a "name" value that can be defined by user
    const val SEQ2 = "SEQ2" // The name will be displayed on the buttons in the motor sequences screen
    const val SEQ3 = "SEQ3"
    const val SEQ4 = "SEQ4"
    const val SEQ5 = "SEQ5"
    const val SEQ6 = "SEQ6"
    const val SEQ7 = "SEQ7"
    const val SEQ8 = "SEQ8"

    //Water
    const val WATER_PUMP = "WaterPump" // values of "On" or "Off" see above todo
    const val WATER_HEATER = "WaterHeater" // values of "On" or "Off" see above todo
    const val TANK_HEATER = "TankHeater" // values of "Enable" or "Disable" see above todo
    const val GREY_PUMP = "GreyPump" // values of "On" or "Off" see above todo"

    //Fridge
    const val FRIDGE = "Fridge" // values of "On" or "Off" see above todo
}