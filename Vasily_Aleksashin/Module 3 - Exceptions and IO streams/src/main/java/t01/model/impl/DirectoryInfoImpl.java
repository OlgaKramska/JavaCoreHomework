package t01.model.impl;

import t01.exception.ModelException;
import t01.model.DirectoryInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class DirectoryInfoImpl implements DirectoryInfo {
    private int countFile = 0;
    private int countDir = 0;
    private long sizeFile = 0;

	@Override
    public Path[] getDirStream(final String path) throws ModelException {
	    validate(path);

	    Path[] paths = null;
	    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(path))){
		    for (Path element : dirStream) {
			    if (paths == null) {
				    paths = new Path[1];
			    } else {
				    paths = Arrays.copyOf(paths, paths.length);
			    }
			    paths[paths.length - 1] = element;
		    }
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    return paths;
	}

	@Override
    public String getDirInfo(final Path[] paths) throws ModelException {
		validate(paths);
        StringBuilder builder = new StringBuilder();
        try {
            for (Path dir : paths) {
                File file = new File(dir.toString());
                builder.append(formatterTime(dir)).append("\t");
                if (file.isDirectory()) {
                    builder.append("<DIR>").append("\t\t");
                    countDir++;
                } else {
                    builder.append(alignFileSize(dir));
                    countFile++;
                    sizeFile += Files.size(dir);
                }
                builder.append("\t").append(file.getName()).append("\n");
            }
            builder.append("\t\t\t\t").append(countDir).append(" directories");
            builder.append("\n\t\t\t\t").append(countFile).append(" files ").append(String.format("%,8d", sizeFile)).append(" byte");
        } catch (InvalidPathException e) {
            throw new ModelException("Path not found");
        } catch (IOException e) {
            throw new ModelException("Can not access to path");
        }
        return builder.toString();
    }

	private void validate(final Object obj) throws ModelException {
		if (obj == null) {
			throw new ModelException("Path can not be NULL");
		}
	}

    private String formatterTime(Path dir) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy  HH.mm.ss");
        FileTime fileTime = Files.getLastModifiedTime(dir);
        return dateFormat.format(fileTime.toMillis());
    }

    private String alignFileSize(Path dir) throws IOException {
        StringBuilder builder = new StringBuilder();
        int length = String.format("%,8d", Files.size(dir)).length();
        for (int index = 0; index < 15 - length; index++) {
            builder.append(" ");
        }
        builder.append(String.format("%,8d", Files.size(dir)));
        return builder.toString();
    }
}