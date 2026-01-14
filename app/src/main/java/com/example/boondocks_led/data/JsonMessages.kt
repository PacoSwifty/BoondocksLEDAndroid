package com.example.boondocks_led.data

import kotlinx.serialization.Serializable

/**
 * This file will describe the shape and use cases of the json messages we can send over BLE.
 * Additionally, it will hold data classes to be serialized for transmission.
 */

//region Set LED
// ----- Set LED -----

/** Type: RGBW -
 * Root Value is a number 1-4 corresponding to an LED Controller's ID
 * White should be mutually exclusive with RGB, and it's value should either be 0 or 255
 *
 */
//  {
//    4: {
//     "R": 255,
//     "G": 0,
//     "B": 0,
//     "W": 0"
//      }
//  }


/**
 * Type: RGB+1 - Use this SetLED call to toggle the +1 channel on or off
 * Root Value is a number 1-4 corresponding to an LED Controller's ID
 * Here you will set the RGB and the +1 (W) in two separate calls, the RGB values for
 * the multicolor strip, and a value of either 0 or 255 in the W channel for the +1 strip
 */

//  {
//    3: {
//       "R": 128,
//       "G": 128,
//       "B": 128
//      }
//  }

//  {
//    3: {
//       "W": 255
//     }
//  }


/**
 * Type: 4 Channel
 * Root value is a number 1-4 corresponding to an LED Controller's ID
 * R,G,B, and W channels control each of the 4 channels independently. You
 * Can toggle any one of them on or off by specifying the channel and giving it a value of 0 or 255
 * The below example turns on the third channel on the controller with ID of 2
 */

//{
//    2: {
//      "B": 255
//    }
//}

@Serializable
data class RGBW(
    val R: Int,
    val G: Int,
    val B: Int,
    val W: Int
)

@Serializable
data class RGB(
    val R: Int,
    val G: Int,
    val B: Int
)

@Serializable
data class SingleChannelChange(
    val channel: String,
    val value: Int
)
//endregion

//region Set Brightness
// ----- Set Brightness -----
//todo: Later we'll update this to be a float from 0 to 1.0f, for now we're using int values 0-3

/**
 * Type: RGBW
 * All 4 channels should get the same brightness value. The root value is the controller's ID
 * For now these values will be 0-3, later we'll pass either float 0-1.0f or int 0-255
 *
 */
//{
//  4: {
//    "R": 3,
//    "G": 3,
//    "B": 3,
//    "W": 3
//  }
//}


/**
 * Type: RGB+1
 * Root is still the controllerId, W refers to setting brightness of just the +1 channel
 *
 */

//todo: find out if we can set brightness of RGB in RGB+1 scenarios

// {
//     1: {
//      "W": 0
//     }
// }


/**
 * Type: 4Chan
 * Root is still controllerId, R,G,B,W correspond to channel 1-4 to set the brightness of. In
 * the below example, we are setting the brightness on the 3rd channel (B) of controller 2 to a value of 1 (25%)
 */

// {
//     "2": {
//       "B": 1
//     }
// }

//endregion

//region Set Type
// ----- Set Type -----
/**
 * Type RGBW: Root is ControllerID, type is one RGBW, name is dynamic, should be a single channel name
 *
 */

//{
//    '1': {
//    'Type': 'RGBW',
//    'Name': 'Cabinet',
//    'ChanNames': {
//        'RGBW': 'Cabinet'
//      }
//    }
//}

/**
 * Type RGB+1: Root is ControllerId, type is RGB+1, name is dynamic, channel names have RGB and W
 */

//{
//    '2': {
//    'Type': 'RGB+1',
//    'Name': 'Cabinet',
//    'ChanNames': {
//        'RGB': 'CabLeft',
//        'W': 'CabRight'
//        }
//    }
//}

/**
 * Type 4Chan: Root is ControllerId, Type is 4Chan, name is dynamic, channels 1-4 are represented by R,G,B, and W, they have dynamic names
 */

//{
//    '1': {
//        'Type': '4Chan',
//        'Name': 'Cabinets',
//        'ChanNames': {
//            'R': 'Cabinet1',
//            'G': 'Cabinet2',
//            'B': 'Cabinet3',
//            'W': 'Cabinet4'
//        }
//    }
//}

// endregion

//region Scene Configuration

//Scene Selection:
//{keyword: value}
//Where keyword is "LEDScene"
//Value is a number of 1 through 4
//e.g. {"LEDScene": "1"}


//Scene Save Configuration:
//{scene#: name}
//Where scene# is a number from 1 to 4 corresponding to the four scenes that can be saved.
//name is the custom name the user entered.  Maximum of 10 characters.  When this is
//invoked, how the LEDs are set on the 4 controllers is saved to a file.
//e.g. {'1': 'ABigName10'}}
//endregion