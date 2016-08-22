<!---
layout: intro
title: SunChat
-->

# Hurry
### A Nearest Bathroom Guiding system Actuate By Smart-Band
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg?maxAge=2592000)]()
[![Packagist](https://img.shields.io/badge/First Commit Date-2015.08-brightgreen.svg)]()
[![Packagist](https://img.shields.io/badge/Last Commit Date-2016.07-brightgreen.svg)]()
[![Packagist](https://img.shields.io/badge/Branch-4-orange.svg)]()
[![Packagist](https://img.shields.io/badge/Commit-50+-red.svg)]()</br>   

Hurry is a open source project that can tell you the nearest toilet. The project is operated under bitbucket, and it moves to github now.    
<p align="center">
  <img src="https://github.com/SunnerLi/Hurry/blob/master/Img/hurry_icon.png" height=128 width=128/>
</p>


## Idea about Hurry

In Taiwan, 2015 is said to be the year that IOT would explore. This project is a simple application about IOT. This project is an open source project. We hope the people who interested in this topic can help us revise this system, and make it beter and better!</br></br>     
     
     
## location

you can just apply this system in Kaohsiung(in Taiwan). Maybe you can add other places you want.</br></br>     
     
     
## How To

This project use BeagleBone-Black as the microprocessor of the smart-band. Two LED light to show the status of the band. The band also have bluetooth dongle to pass the data that the APP would receive. On the other hand, the APP would detect where you are and show you the way after smart-phone accept the bluetooth signal.</br></br>     
     
     
## Type of toilet    
```
*  KFC, Mcdonald    
*  Popular Gas-Station    
*  MRT    
*  Popular Convenience Store    
*  Popular department Store    
*  Hope you add some...    
```

## Equipment of Smart Band

the smart band is implemented by beaglebone-black devC. The program is written by C. The environment is Ubuntu 14.04. 

Notice: The smart band communicate with the smart phone over bluetooth. The Bluetooth transfer program might not work at some linux system. It depend on which the OS support the serial protocol.    </br></br>     
     
     
License
---------------------
    The MIT License (MIT)
    Copyright (c) 2015 - SunnerLi

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
    and associated   documentation files (the "Software"), to deal in the Software without 
    restriction, including without limitation the rights to use, copy, modify, merge, publish, 
    distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom 
    the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or 
    substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
    BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
