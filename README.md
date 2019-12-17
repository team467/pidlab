<header>
    <h1>PID LAB </h1>
    <p><b>Table of Contents</b></p>
    <ul>
        <li><a href="#setup">Setup</a></li>
        <li><a href="#pidedit">How to edit PIDs</a></li>
        <li><a href="#tFormat"> Telemetry format</a></li>
    </ul>
</header>
<body>
    <h2 id="setup">Setup</h2>
    <hr>
    <p>In order to set up this up you must clone <a href="https://github.com/team467/pidlab" target="_blank" >this repository</a> and open this code in an <b>IDE</b> of you choice or <b>VSCODE</b></p>
    <p> In order for the simulator to simulate the motor properties of the robot you must first grab data from the robot in a specific form. This form can be found <a href="#tFormat"> here</a></p>
<br />
    <p> Then change the file name in the file Index.java to the correct file. Once you have done this both ImpulseResponseChart.java and PIDResponseChart.java have a main function which can be run each making 2 different graphs for each side. How do you use these graphs and what do they mean? Find out in the next section!</p>
<br />
    <h2 id="pidedit"> How to edit PIDs</h2>
    <hr>
    <b> pt. I </b>
    <p>In order to get this lab working you need to the correct impulse variables from the the Impulse Response charts. Run ImpulseResponseChart.java and 2 screens will pop up. Each screen corresponds to a side which you should have determined in the telemetry formatting step. Change the variables in the "Motor Properties" box accordlingly so that the recorded speed and position lines match up with the simulated lines. Write down or memorize the values that you have determined for the motor properties. <p>
    <b>pt. II</b>
    <p>Now that you have the motor properties variables you should run PIDResponseChart.java plug in your determined property variables in the boxes from before. Adjust the plot settings accordingly. Now you are all set to tune the PIDs!</P>
    <i>IMPORTANT: each side could have different motor qualities so make sure you do both sides</i>
<br />
    <h2 id="tFormat">Telemetry format </h2>
    <hr>
    <p><b>In order for PID simulator to work correctly you must have a telemetry log file formatted correctly.</b></p>
<br />
    <p><em>the correct format should be:</em></p>
<br />
<br />
    <p> In ASCII encoding</p>
        <code>time%20info%20telemetry%20%2D%20input%2Cspeed(side1)%2Cposition(side1)%2Cspeed(side2)%2Cposition(side2)</code>
<br />
<br />
    <p>Example:</p>
        <code> 215399ms INFO telemetry - 0.781250,0.000000,0.042155,-4897502,-0.034627,4.966756</code>
<body>









