package com.darkrockstudios.apps.adventurerocksdownloader.photos

import android.Manifest
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.Klaxon
import com.darkrockstudios.apps.adventurerocksdownloader.*
import com.darkrockstudios.apps.adventurerocksdownloader.database.AppDatabase
import com.darkrockstudios.apps.adventurerocksdownloader.database.Photo360
import com.darkrockstudios.apps.adventurerocksdownloader.databinding.PhotosFragmentBinding
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.log4k.d
import com.log4k.e
import com.log4k.i
import com.log4k.w
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.photos_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject


class PhotosFragment: Fragment(), DownloadFileListener
{
	private val viewModel by viewModels<PhotosViewModel>()
	private val photosAdapter = ItemAdapter<PhotoListItem>()
	private val adapter = FastAdapter.with(photosAdapter)

	private val database by inject<AppDatabase>()
	private val http by inject<OkHttpClient>()
	private val json by inject<Klaxon>()

	private val fileDownloader by lazy { FilesDownloader(requireContext()) }
	private var binding: PhotosFragmentBinding? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		loadPhotoIndex()

		fileDownloader.addListener(this)

		viewModel.viewModelScope.launch(Dispatchers.IO) {
			var abort = false

			val result = permissionsBuilder(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build().sendSuspend()
			result.forEach { r ->
				if(r is PermissionStatus.Denied) abort = true
			}
		}
	}

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?): View?
	{
		binding = PhotosFragmentBinding.inflate(inflater, container, false)
		return binding!!.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		super.onViewCreated(view, savedInstanceState)

		adapter.setHasStableIds(true)

		photos_list.adapter = adapter

		adapter.onClickListener = { _, _, item, _ ->
			downloadPhoto(item.photo)

			true
		}

		database.photoDao().loadAll().observe(viewLifecycleOwner, ::updateEntries)

		binding?.apply {
			fab.setOnClickListener {
				context?.let { ctx ->
					MaterialAlertDialogBuilder(ctx)
							.setTitle(R.string.confirm_download_all_title)
							.setPositiveButton(android.R.string.yes) { _, _ -> downloadAll() }
							.setNegativeButton(android.R.string.no, null)
							.show()
				}
			}
		}

		updateDownloadProgress()
	}

	override fun onDestroyView()
	{
		super.onDestroyView()
		binding = null
	}

	private fun updateEntries(entries: List<Photo360>)
	{
		photosAdapter.clear()
		//val items = entries.sortedByDescending { it.start }.map { FastEntryItem(it) }
		val items = entries.map { PhotoListItem(it) }
		photosAdapter.add(items)
		adapter.notifyAdapterDataSetChanged()
	}

	private fun downloadAll()
	{
		// Scan all downloaded photos
		//photosAdapter.adapterItems.map { it.photo }.filter { it.isDownloaded() }.forEach { scanPhoto(it.file().absolutePath) }

		val toDownload = photosAdapter.adapterItems
				.map { it.photo }
				.filter { !it.isDownloaded() }

		if(toDownload.isEmpty()) view?.let { v -> Snackbar.make(v, R.string.sb_download_all_done, Snackbar.LENGTH_SHORT).show() }

		toDownload.forEach(::downloadPhoto)
	}

	private fun downloadPhoto(photo: Photo360)
	{
		if(photo.isDownloaded())
		{
			view?.let { v -> Snackbar.make(v, R.string.sb_download_already_complete, Snackbar.LENGTH_SHORT).show() }
			return
		}

		photo.file().mkdirs()

		val photoFile = photo.file()
		fileDownloader.download(photoFile.absolutePath, photo.photo_url)

		updateDownloadProgress()
	}

	private fun loadPhotoIndex()
	{
		viewModel.viewModelScope.launch(Dispatchers.IO) {
			val request = Request.Builder().url("https://wethinkadventure.rocks/api/360photos").build()
			http.newCall(request).execute().use { response ->
				if(response.isSuccessful)
				{
					response.body?.use { body ->
						val bodyJson = body.string()
						d("bodyJson: $bodyJson")
						val photos = json.parse<PhotosResponse>(bodyJson)
						if(photos != null)
						{
							database.photoDao().insertAll(photos.photospheres)
							i("Photos inserted!")
						}
						else
						{
							e("Failed to parse response body")
						}
					}
				}
				else
				{
					w("Failed to load photo index: ${response.code}")
				}
			}
		}
	}

	override fun onDownloadFile(result: DownloadResult)
	{
		if(result.hasError)
		{
			view?.let { v -> Snackbar.make(v, R.string.sb_download_failed, Snackbar.LENGTH_SHORT).show() }
		}
		else
		{
			scanPhoto(result.path)
			view?.let { v -> Snackbar.make(v, R.string.sb_download_complete, Snackbar.LENGTH_SHORT).show() }
		}

		updateDownloadProgress()
		adapter.notifyAdapterDataSetChanged()
	}

	private fun scanPhoto(path: String)
	{
		context?.let { ctx ->
			val mime = MimeTypeMap.getFileExtensionFromUrl(path)
			MediaScannerConnection.scanFile(ctx, arrayOf(path), arrayOf(mime)) { path, _ ->
				d("Scan complete for: $path")
			}
		}
	}

	override fun onProgress(data: DownloadData, progress: Float)
	{
		//updateDownloadProgress()
	}

	private fun updateDownloadProgress()
	{
		val numDownloads = fileDownloader.numActiveDownloads()
		val downloading = numDownloads > 0

		binding?.apply {
			downloadProgressBar.visibility = if(downloading) View.VISIBLE else View.INVISIBLE
			numDownloadsView.text = getString(R.string.active_downloads, numDownloads)
		}
	}
}