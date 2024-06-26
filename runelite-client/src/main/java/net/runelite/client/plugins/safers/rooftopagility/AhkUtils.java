package net.runelite.client.plugins.safers.rooftopagility;

import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class AhkUtils
{
	public static boolean isOnInventoryTab(Client client) {
		return client.getVarcIntValue(VarClientInt.INVENTORY_TAB) == 3;
	}
	public static boolean isOnMagicTab(Client client) {
		return client.getVarcIntValue(VarClientInt.INVENTORY_TAB) == 6;
	}

	public static boolean highlightMagicTab(Client client,
											Graphics2D graphics,
											int size,
											Color color
	) {
		if (isOnMagicTab(client)) return false;
		Widget tab1 = client.getWidget(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB);
		Widget tab2 = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_MAGIC_TAB);

		SquareOverlay.drawRandomBounds(graphics, tab1, size, color);
		SquareOverlay.drawRandomBounds(graphics, tab2, size, color);
		return true;
	}
	public static boolean highlightInventoryTab(Client client,
											Graphics2D graphics,
											int size,
											Color color
	) {
		if (isOnInventoryTab(client)) return false;
		Widget tab1 = client.getWidget(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
		Widget tab2 = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_INVENTORY_TAB);

		SquareOverlay.drawRandomBounds(graphics, tab1, size, color);
		SquareOverlay.drawRandomBounds(graphics, tab2, size, color);
		return true;
	}

	public static String colorToHex(Color color) {
		return "0x"+Integer.toHexString(color.getRGB()).substring(2);
	}
	public static void runScript(String scriptName, String script, String ahkLocation) {
		try
		{
			Path tempDir = Files.createTempDirectory(scriptName);
			File staticNameFile = new File(tempDir.toFile()+"\\" +scriptName+".ahk");
			if (!staticNameFile.exists())
				staticNameFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(staticNameFile));
			writer.write(script);
			writer.close();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(ahkLocation + " " + staticNameFile.getAbsolutePath());
			Thread.sleep(1000);
			staticNameFile.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static String DEFAULT_AHK_LOCATION = "C:\\Program Files\\AutoHotkey\\AutoHotkey.exe";

	public static String colorClickScript(String message, String color) {


	return "#Requires Autohotkey v1.1\n" + "#SingleInstance Force\n" +
		"#NoEnv\n" +
		"SendMode Input\n" +
		"SetWorkingDir %A_ScriptDir%\n" +
		"SetBatchLines, -1\n" +
		"SetMouseDelay, -1\n" +
		"Process, Priority,, H\n" +
		"\n" +
		"MsgBox, 0,, "+message+"`nEsc to stop script\n" +
		"InputBox, clickDelay, Set click delay, Set click delay in milliseconds,,,,,,,,3000\n" +
		"\n" +
		"loop {\n" +
		"\tif (click_color("+color+")) {\n" +
		"\t\tSleep, rand_range(clickDelay,clickDelay*1.1)\n" +
		"\t}\n" +
		"\tsleep, rand_range(300,500)\n" +
		"}\n" +
		"return\n" +
		"\n" +
		"Esc::\n" +
		"ExitApp\n" +
		"return\n" +
		"\n" +
		"click_color(color) {\n" +
		"\tTooltip\n" +
		"\tPixelSearch, FoundX, FoundY, 0, 0, % A_ScreenWidth, % A_ScreenHeight, color, 5, Fast RGB\n" +
		"\tIf (ErrorLevel = 0) {\n" +
		"\t\tsleep_click(FoundX,FoundY)\n" +
		"\t\treturn True\n" +
		"\t}\n" +
		"\treturn False\n" +
		"}\n" +
		"\n" +
		"sleep_click(x, y) {\n" +
		"\tToolTip\n" +
		"\tx += target_random(-7,0,7)\n" +
		"\ty += target_random(-7,0,7)\n" +
		"\tMoveMouse(x, y)\n" +
		"\tMouseMove, %x%, %y%, 50\n" +
		"\tSleep, rand_range(50,70)\n" +
		"\tMouseClick, Left\n" +
		"\tSleep, rand_range(20,40)\n" +
		"}\n" +
		"\n" +
		"MoveMouse(x, y, speed:= 1.3, RD:= \"\"){\n" +
		"\n" +
		"    ;Random, rxRan,-10,10  ; here you can randomize your destination coordinates\n" +
		"    ;Random, ryRan,-10,10  ; so you don't need to randomize them in your main script\n" +
		"    ;x:= x + rxRan         ; uncomment the beginning of these four lines\n" +
		"    ;y:= y + ryRan         ; if you want random destinations\n" +
		"\n" +
		"    if(RD == \"RD\")\n" +
		"\t{\n" +
		"        goRelative(x,y,speed)\n" +
		"    }\n" +
		"    else\n" +
		"\t{\n" +
		"        goStandard(x,y,speed)\n" +
		"    }\n" +
		"}\n" +
		";---------------------- no need to change anything below ------------------------;\n" +
		"WindMouse(xs, ys, xe, ye, gravity, wind, minWait, maxWait, maxStep, targetArea, sleepsArray){\n" +
		"windX:= 0, windY:= 0\n" +
		"veloX:= 0, veloY:= 0\n" +
		"newX:= Round(xs)\n" +
		"newY:= Round(ys)\n" +
		"waitDiff:= maxWait - minWait\n" +
		"sqrt2:= Sqrt(2)\n" +
		"sqrt3:= Sqrt(3)\n" +
		"sqrt5:= Sqrt(5)\n" +
		"dist:= Hypot(xe - xs, ye - ys)\n" +
		"i:= 1\n" +
		"stepVar:= maxStep\n" +
		"Loop{\n" +
		"\twind:= Min(wind, dist)\n" +
		"\tif(dist >= targetArea){\n" +
		"\t\twindX:= windX / sqrt3 + (random(round(wind) * 2 + 1) - wind) / sqrt5\n" +
		"\t\twindY:= windY / sqrt3 + (random(round(wind) * 2 + 1) - wind) / sqrt5\n" +
		"\t\tmaxStep:= RandomWeight(stepVar/2, (stepVar+(stepVar/2))/2, stepVar)\n" +
		"\t\t}else{\n" +
		"\t\t\twindX:= windX / sqrt2\n" +
		"\t\t\twindY:= windY / sqrt2\n" +
		"\t\t\tif(maxStep < 3){\n" +
		"\t\t\t\tmaxStep:= 1\n" +
		"\t\t\t}else{\n" +
		"\t\t\t\tmaxStep:= maxStep / 3\n" +
		"\t\t\t}\n" +
		"\t\t}\n" +
		"\t\tveloX += windX\n" +
		"\t\tveloY += windY\n" +
		"\t\tveloX:= veloX + gravity * ( xe - xs ) / dist\n" +
		"\t\tveloY:= veloY + gravity * ( ye - ys ) / dist\n" +
		"\t\tif(Hypot(veloX, veloY) > maxStep){\n" +
		"\t\t\trandomDist:= maxStep / 2 + (Round(random(maxStep)) / 2)\n" +
		"\t\t\tveloMag:= Hypot(veloX, veloY)\n" +
		"\t\t\tveloX:= ( veloX / veloMag ) * randomDist\n" +
		"\t\t\tveloY:= ( veloY / veloMag ) * randomDist\n" +
		"\t\t}\n" +
		"\t\toldX:= Round(xs)\n" +
		"\t\toldY:= Round(ys)\n" +
		"\t\txs:= xs + veloX\n" +
		"\t\tys:= ys + veloY\n" +
		"\t\tdist:= Hypot(xe - xs, ye - ys)\n" +
		"\t\tif(dist <= 1){\n" +
		"\t\t\tBreak\n" +
		"\t\t}\n" +
		"\t\tnewX:= Round(xs)\n" +
		"\t\tnewY:= Round(ys)\n" +
		"\t\tif(oldX != newX) or (oldY != newY){\n" +
		"\t\t\tMouseMove, newX, newY\n" +
		"\t\t}\n" +
		"\t\tstep:= Hypot(xs - oldX, ys - oldY)\n" +
		"\t\tc:= sleepsArray.Count()\n" +
		"\t\tif(i > c){\n" +
		"\t\t\tlastSleeps:= Round(sleepsArray[c])\n" +
		"\t\t\tRandom, w, lastSleeps, lastSleeps+1\n" +
		"\t\t\twait:= Max(Round(abs(w)),1)\n" +
		"\t\t\tSleep(wait)\n" +
		"\t\t}else{\n" +
		"\t\t\twaitSleep:= Round(sleepsArray[i])\n" +
		"\t\t\tRandom, w, waitSleep, waitSleep+1\n" +
		"\t\t\twait:= Max(Round(abs(w)),1)\n" +
		"\t\t\tSleep(wait)\n" +
		"\t\t\ti++\n" +
		"\t\t}\n" +
		"\t}\n" +
		"endX:= Round(xe)\n" +
		"endY:= Round(ye)\n" +
		"\tif(endX != newX) or (endY != newY){\n" +
		"\t\tMouseMove, endX, endY\n" +
		"    }\n" +
		"i:= 1\n" +
		"}\n" +
		"WindMouse2(xs, ys, xe, ye, gravity, wind, minWait, maxWait, maxStep, targetArea){\n" +
		"windX:= 0, windY:= 0\n" +
		"veloX:= 0, veloY:= 0\n" +
		"newX:= Round(xs)\n" +
		"newY:= Round(ys)\n" +
		"waitDiff:= maxWait - minWait\n" +
		"sqrt2:= Sqrt(2)\n" +
		"sqrt3:= Sqrt(3)\n" +
		"sqrt5:= Sqrt(5)\n" +
		"dist:= Hypot(xe - xs, ye - ys)\n" +
		"newArr:=[]\n" +
		"stepVar:= maxStep\n" +
		"Loop{\n" +
		"    wind:= Min(wind, dist)\n" +
		"\t\tif(dist >= targetArea){\n" +
		"\t\t\twindX:= windX / sqrt3 + (random(round(wind) * 2 + 1) - wind) / sqrt5\n" +
		"\t\t\twindY:= windY / sqrt3 + (random(round(wind) * 2 + 1) - wind) / sqrt5\n" +
		"\t\t\tmaxStep:= RandomWeight(stepVar/2, (stepVar+(stepVar/2))/2, stepVar)\n" +
		"        }else{\n" +
		"            windX:= windX / sqrt2\n" +
		"            windY:= windY / sqrt2\n" +
		"            if(maxStep < 3){\n" +
		"                maxStep:= 1\n" +
		"            }else{\n" +
		"                maxStep:= maxStep / 3\n" +
		"            }\n" +
		"        }\n" +
		"        veloX += windX\n" +
		"        veloY += windY\n" +
		"        veloX:= veloX + gravity * ( xe - xs ) / dist\n" +
		"        veloY:= veloY + gravity * ( ye - ys ) / dist\n" +
		"        if(Hypot(veloX, veloY) > maxStep){\n" +
		"            randomDist:= maxStep / 2 + (Round(random(maxStep)) / 2)\n" +
		"            veloMag:= Hypot(veloX, veloY)\n" +
		"            veloX:= ( veloX / veloMag ) * randomDist\n" +
		"            veloY:= ( veloY / veloMag ) * randomDist\n" +
		"        }\n" +
		"        oldX:= Round(xs)\n" +
		"        oldY:= Round(ys)\n" +
		"        xs:= xs + veloX\n" +
		"        ys:= ys + veloY\n" +
		"        dist:= Hypot(xe - xs, ye - ys)\n" +
		"\t\tif(dist <= 1){\n" +
		"\t\t\tBreak\n" +
		"\t\t}\n" +
		"        newX:= Round(xs)\n" +
		"        newY:= Round(ys)\n" +
		"        if(oldX != newX) or (oldY != newY){\n" +
		"            p:=0\n" +
		"        }\n" +
		"        step:= Hypot(xs - oldX, ys - oldY)\n" +
		"\t\tmean:= Round(waitDiff * (step / maxStep) + minWait)/7\n" +
		"\t\twait:= Muller((mean)/2,(mean)/2.718281)\n" +
		"\t\tnewArr.Push(wait)\n" +
		"    }\n" +
		"endX:= Round(xe)\n" +
		"endY:= Round(ye)\n" +
		"    if(endX != newX) or (endY != newY){\n" +
		"        p:=0\n" +
		"    }\n" +
		"Return newArr\n" +
		"}\n" +
		"Hypot(dx, dy){\n" +
		"    return Sqrt(dx * dx + dy * dy)\n" +
		"}\n" +
		"random(n){\n" +
		"\trandom, out, 0, n\n" +
		"\treturn % out\n" +
		"}\n" +
		"Sleep(s)\n" +
		"{\n" +
		"    SetBatchLines, -1\n" +
		"    DllCall(\"QueryPerformanceFrequency\", \"Int64*\", freq)\n" +
		"    DllCall(\"QueryPerformanceCounter\", \"Int64*\", CounterBefore)\n" +
		"    While (((counterAfter - CounterBefore) / freq * 1000) < s)\n" +
		"        DllCall(\"QueryPerformanceCounter\", \"Int64*\", CounterAfter)\n" +
		"    return ((counterAfter - CounterBefore) / freq * 1000)\n" +
		"}\n" +
		"Muller(m,s){\n" +
		"   Static i, Y\n" +
		"   If (i := !i){\n" +
		"      Random U, 0, 1.0\n" +
		"      Random V, 0, 6.2831853071795862\n" +
		"      U := sqrt(-2*ln(U))*s\n" +
		"      Y := m + U*sin(V)\n" +
		"      Return m + U*cos(V)\n" +
		"   }\n" +
		"   Return Y\n" +
		"}\n" +
		"SortArray(Array, Order=\"A\"){\n" +
		"    MaxIndex := ObjMaxIndex(Array)\n" +
		"    If (Order = \"R\"){\n" +
		"        count := 0\n" +
		"        Loop, % MaxIndex\n" +
		"            ObjInsert(Array, ObjRemove(Array, MaxIndex - count++))\n" +
		"        Return\n" +
		"    }\n" +
		"    Partitions := \"|\" ObjMinIndex(Array) \",\" MaxIndex\n" +
		"    Loop{\n" +
		"        comma := InStr(this_partition := SubStr(Partitions, InStr(Partitions, \"|\", False, 0)+1), \",\")\n" +
		"        spos := pivot := SubStr(this_partition, 1, comma-1) , epos := SubStr(this_partition, comma+1)\n" +
		"        if (Order = \"A\"){\n" +
		"            Loop, % epos - spos{\n" +
		"                if (Array[pivot] > Array[A_Index+spos])\n" +
		"                    ObjInsert(Array, pivot++, ObjRemove(Array, A_Index+spos))\n" +
		"            }\n" +
		"        }else{\n" +
		"            Loop, % epos - spos{\n" +
		"                if (Array[pivot] < Array[A_Index+spos])\n" +
		"                    ObjInsert(Array, pivot++, ObjRemove(Array, A_Index+spos))\n" +
		"            }\n" +
		"        }\n" +
		"        Partitions := SubStr(Partitions, 1, InStr(Partitions, \"|\", False, 0)-1)\n" +
		"        if (pivot - spos) > 1\n" +
		"            Partitions .= \"|\" spos \",\" pivot-1\n" +
		"        if (epos - pivot) > 1\n" +
		"            Partitions .= \"|\" pivot+1 \",\" epos\n" +
		"    }Until !Partitions\n" +
		"}\n" +
		"RandomWeight(min,target,max){\n" +
		"Random,Rmin,min,target\n" +
		"Random,Rmax,target,max\n" +
		"Random,weighted,Rmin,Rmax\n" +
		"Return, weighted\n" +
		"}\n" +
		"goStandard(x, y, speed){\n" +
		"MouseGetPos, xpos, ypos\n" +
		"distance:= (Sqrt(Hypot(x-xpos,y-ypos)))*speed\n" +
		"dynamicSpeed:= (1/distance)*60\n" +
		"Random, finalSpeed, dynamicSpeed, dynamicSpeed + 0.8\n" +
		"stepArea:= Max(( finalSpeed / 2 + distance ) / 10, 0.1)\n" +
		"newArr:=[]\n" +
		"newArr:= WindMouse2(xpos, ypos, x, y, 10, 3, finalSpeed * 10, finalSpeed * 12, stepArea * 11, stepArea * 7)\n" +
		"SortArray(newArr, \"D\")\n" +
		"c:= newArr.Count()\n" +
		"g:= c/2\n" +
		"\tLoop, %g%{\n" +
		"\tnewArr.RemoveAt(c)\n" +
		"\tc--\n" +
		"\t}\n" +
		"newClone:=[]\n" +
		"newClone:= newArr.Clone()\n" +
		"SortArray(newClone, \"A\")\n" +
		"newArr.Push(newClone*)\n" +
		"WindMouse(xpos, ypos, x, y, 10, 3, finalSpeed * 10, finalSpeed * 12, stepArea * 11, stepArea * 7, newArr)\n" +
		"newArr:=[]\n" +
		"}\n" +
		"goRelative(x, y, speed){\n" +
		"MouseGetPos, xpos, ypos\n" +
		"distance:= (Sqrt(Hypot((xpos+abs(x))-xpos,(ypos+abs(y))-ypos)))*speed\n" +
		"dynamicSpeed:= (1/distance)*60\n" +
		"Random, finalSpeed, dynamicSpeed, dynamicSpeed + 0.8\n" +
		"stepArea:= Max(( finalSpeed / 2 + distance ) / 10, 0.1)\n" +
		"newArr:=[]\n" +
		"newArr:= WindMouse2(xpos, ypos, xpos+x, ypos+y, 10, 3, finalSpeed * 10, finalSpeed * 12, stepArea * 11, stepArea * 7)\n" +
		"SortArray(newArr, \"D\")\n" +
		"c:= newArr.Count()\n" +
		"g:= c/2\n" +
		"\tLoop, %g%{\n" +
		"\tnewArr.RemoveAt(c)\n" +
		"\tc--\n" +
		"\t}\n" +
		"newClone:=[]\n" +
		"newClone:= newArr.Clone()\n" +
		"SortArray(newClone, \"A\")\n" +
		"WindMouse(xpos, ypos, xpos+x, ypos+y, 10, 3, finalSpeed * 10, finalSpeed * 12, stepArea * 11, stepArea * 7, newArr)\n" +
		"newArr:=[]\n" +
		"}\n" +
		"\n" +
		"rand_bool() {\n" +
		"\tRandom, r, 0, 1\n" +
		"\treturn (r = 1)\n" +
		"}\n" +
		"\n" +
		"rand(range=5) {\n" +
		"\tRandom, r, -%range%, %range%\n" +
		"\treturn r\n" +
		"}\n" +
		"\n" +
		"rand_range(min, max) {\n" +
		"\tRandom, r, %min%, %max%\n" +
		"\treturn r\n" +
		"}\n" +
		"\n" +
		"rand_range_center(center, min, max) {\n" +
		"\tRandom, r, %min%, %max%\n" +
		"\tcenter += r\n" +
		"\treturn center\n" +
		"}\n" +
		"\n" +
		"set_rand_ref(ByRef var, min, max) {\n" +
		"\tRandom, r, %min%, %max%\n" +
		"\tvar = r\n" +
		"\treturn var\n" +
		"}\n" +
		"\n" +
		"shift_rand_ref(ByRef var, min, max) {\n" +
		"\tRandom, r, %min%, %max%\n" +
		"\tvar += r\n" +
		"\treturn var\n" +
		"}\n" +
		"\n" +
		"binomial(center, range, loops=10) {\n" +
		"\tloop %loops% {\n" +
		"\t\tRandom r, 0, % range*2/loops\n" +
		"\t\tsum += r\n" +
		"\t}\n" +
		"\treturn sum-range+center\n" +
		"}\n" +
		"\n" +
		"target_random(min, target, max){\n" +
		"\tRandom, lower, min, target\n" +
		"\tRandom, upper, target, max\n" +
		"\tRandom, weighted, lower, upper\n" +
		"\tReturn, weighted\n" +
		"}\n" +
		"\n" +
		"; https://autohotkey.com/board/topic/64617-normaly-distributed-random-number/\n" +
		"NormalRand(x,y,int=1) { ;x lower y upper int for integer return\n" +
		"Loop 12\n" +
		" {\n" +
		" Random, var,0.0,1\n" +
		" Num+=var\n" +
		" }\n" +
		"norm := Round((y+x)/2+((Num-6)*(y-x))/6)\n" +
		"Return norm < x ? x : norm > y ? y : norm\n" +
		"}\n" +
		"\n" +
		"NormalRandd(x,target,y) {\n" +
		"\teps := 0.5\n" +
		"\tfirst := NormalRand(x,y) + target\n" +
		"\n" +
		"\tif (first < x OR first > y) {\n" +
		"\t\tfirst := rand_range(x,y)\n" +
		"\t}\n" +
		"\tloop 5 {\n" +
		"\t\tdiff := target-first\n" +
		"\t\tRandom, r, 0.0, 1.0\n" +
		"\t\tif (first < target) {\n" +
		"\t\t\tratio := Max((diff/(target - x))**2,0.10)\n" +
		"\t\t} else {\n" +
		"\t\t\tratio := Max((diff/(y - target))**2,0.10)\n" +
		"\t\t}\n" +
		"\t\tif (r <= ratio) {\n" +
		"\t\t\tfirst := rand_range(x,y)\n" +
		"\t\t}\n" +
		"\t}\n" +
		"\treturn first\n" +
		"}\n" +
		"\n" +
		"; https://www.autohotkey.com/boards/viewtopic.php?p=172653#p172653\n" +
		"GetWeightedRandom(varName, base, diff) {\n" +
		"    static prior_base := {}\n" +
		"    if(prior_base[varName] == \"\")\n" +
		"        prior_base[varName] := base\n" +
		"    Random, random_base, % (prior_base[varName] - diff), % (prior_base[varName] + diff)\n" +
		"    next_random := (base + random_base) / 2\n" +
		"    prior_base[varName] := next_random\n" +
		"    return Round(next_random)\n" +
		"}\n" +
		"\n" +
		"TripleBoxRandom(base, max_variance) {\n" +
		"    inner := (max_variance / 3)\n" +
		"    outer := inner * 2\n" +
		"    orbit := inner * 3\n" +
		"\n" +
		"    low_inner := (base - inner), high_inner := (base + inner)\n" +
		"    low_outer := (base - outer), high_outer := (base + outer)\n" +
		"    low_orbit := (base - orbit), high_orbit := (base + orbit)\n" +
		"\n" +
		"    Random, rng_base, %low_inner%, %high_inner%\n" +
		"    ;if(rng_base > (base + (inner / 2)) or rng_base < (base - (inner / 2))) {\n" +
		"        Random, roll, 1, 2\n" +
		"        if(roll > 1) {\n" +
		"            Random, rng_base, %low_outer%, %high_outer%\n" +
		"            ;if(rng_base > (base + (outer / 2)) or rng_base < (base - (outer / 2))) {\n" +
		"                Random, roll, 1, 2\n" +
		"                if(roll > 1) {\n" +
		"                    Random, rng_base, %low_orbit%, %high_orbit%\n" +
		"                }\n" +
		"            ;}\n" +
		"        }\n" +
		"    ;}\n" +
		"    return rng_base\n" +
		"}";
	}
}
