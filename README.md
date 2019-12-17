<header>
<h1>PID LAB </h1>
<h2>Table of Contents</h2>
<ul>
<li><a href="#setup">Setup</a></li>
<li>Use</li>
<li><a href="#pidedit">How to edit PIDs</a></li>
<li>What does this mean?</li>
<li><a href="#tFormat"> Telemetry format</a></li>
</ul>
</header>
<h2 id="setup">Setup</h2>
<hr>
<p>In order to set up this up must clone <a href="https://github.com/team467/pidlab" target="_blank" >this repository</a> and open this code in an <b>IDE</b> of you choice or <b>VSCODE</b></p>
<p> In order for the simulator to simulate the motor properties of the robot you must first grab data from the robot in a specific form. This form can be found <a href="#tFormat"> here</a></p>
<br />
<p> Then change the file name in the file Index.java to the correct file. Once you have done this both ImpulseResponseChart.java and PIDResponseChart.java have a main function which can be run each making 2 different graphs for each side. How do you use these graphs and what do they mean? Find out in the next section!</p>
<br />
<h2 id="pidedit"> How to edit PIDs</h2>
<hr>
<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
<br />
<h2 id="tFormat">Telemetry format </h2>
<hr>
<p><b>In order for PID simulator to work correctly you must have a telemetry log file formatted correctly.</b></p>
<br />
<p><em>the correct format should be:</em></p>
<p> In ASCII encoding</p>
<code>time%20info%20telemetry%20%2D%20input%2Cspeed(side1)%2Cposition(side1)%2Cspeed(side2)%2Cposition(side2)</code>
<br />
<p>Example:</p>
<code> 215399ms INFO telemetry - 0.781250,0.000000,0.042155,-4897502,-0.034627,4.966756</code>










