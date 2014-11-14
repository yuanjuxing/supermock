/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.pptest.ppmock.common;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class BinaryFile {

	private final File file;

	public BinaryFile(final String filePath) {
		file = new File(filePath);
	}

	public BinaryFile(final File file) {
		this.file = file;
	}
	
	public byte[] readContents() {
		try {
			final byte[] contents = Files.toByteArray(file);
			return contents;
		} catch (final IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public String name() {
		return file.getName();
	}
	
	@Override
	public String toString() {
		return file.getName();
	}
}
