# e2sedit
Electribe Sampler Editor (by Dave Schroeter)

## installation

There really isn't any. Just download e2sEdit.jar (after donating, of course!) and drop it next to an .all file. The default is e2sSample.all. If the application is in the same directory as the default, it will load that file. Note that you can duplicate and rename .all files, and e2sEdit can open and edit them. Also note that you can place it directly in the KORG/electribe sampler/Sample directory on your SD card. I don't recommend this for heavy usage, as this application makes many file writes and creates and deletes files quite often.

## usage

MAKE BACKUPS OF YOUR .ALL FILES! Once you have an .all loaded, you can edit several fields of each sample in the top pane. The factory samples can be edited, deleted, or exported. The user sample slots (beginning at 501) can load new samples which can be edited, deleted, replaced, or exported. All samples can be Played (auditioned), although this feature is hit-or-miss. The code has trouble with really short or really long samples and samples at crazy bitrates (BoostSaw, a factory sample, has a bitrate of 411357!). There is no saving; when you make an edit, the change is done right away. When you are done editing, just close the program and your .all is ready to load. For most fields, if you do something out of bounds, you will get an error message. If you do something weird that I didn't think of, the program will wig out, delete some cherished pictures, and crash into the sun. So send me a note about the bug, and don't forget to MAKE BACKUPS OF YOUR .ALL FILES!

## categories

One of my favorite surprises is that you can edit the category away from User, and the sampler handles it just fine. You can then group your samples together by type, and the Shift-Oscillator scroll will jump between them! Note that the knob will always go to the next different category, so be sure to keep like samples adjacent.

## column headers

A# = Absolute sample number, just ignore it. Lp = Loop status, 0 for loop and 1 for one-shot. St = Stereo. Ld = Loudness aka Play Level. Tn = Tuning.

## loop button

Does what it says - sets the loop endpoint and a special flag to 0. Great for single-cycle waveforms that you want to hold down and play. Once looped, another click gets you back to one-shot land.

## poorly formatted .wav files

All .wav files have a header block that begins with RIFF. For most of them, this header is well-formatted, but I came across some .wavs with some really non-standard formatting. e2sEdit relies on this data and will fail ungracefully if you try to load one. If you are having issues with certain files, a simple re-export with Audacity or similar will solve it. This is also a great way to save space; in one case, exporting in Audacity took a .wav from 70kb to 3kb!

## remaining space calculations

The text for how many seconds of sample time you have left should be viewed as a guesstimate. The nice thing is that the sampler gives an error message if you go over, and e2sEdit is a really great way to delete extra samples!

## category drop-down box

The drop-down box to set the category doesn't select the row of the sample for editing. Just click on the sample name and you should be golden.

## about

I wrote this program for myself. I really love the electribe sampler but did not love the sample-loading process, and no one seemed to be working on software for it.

## using Java / horrible coding / poor planning / grammatical errors / bugs

I am a complete noob to coding applications. As in, this is my first program. Also, most of the work was done late at night after my three young children finally went to bed, so most errors can be chalked up to sleep deprivation. If you find a mistake / bug / place to improve code, please feel free to contact me!

## FAQs

I'll add them as I get them! If you have one, please contact me: flosaic at gmail dot com 
