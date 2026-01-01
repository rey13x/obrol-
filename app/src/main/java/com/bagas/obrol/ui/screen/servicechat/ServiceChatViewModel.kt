package com.bagas.obrol.ui.screen.servicechat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.R
import com.bagas.obrol.domain.repository.OwnProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServiceChatUiState(
    val conversation: List<ServiceChatMessage> = emptyList(),
    val username: String = "Pengguna",
    val isQuestionSheetVisible: Boolean = false
)

class ServiceChatViewModel(
    private val ownProfileRepository: OwnProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceChatUiState())
    val uiState: StateFlow<ServiceChatUiState> = _uiState.asStateFlow()

    val allQuestions = listOf(
        "Bagaimana cara menambahkan kontak?",
        "Bagaimana agar pesan tetap masuk ke saya?",
        "Berapa jarak Obrol+ supaya tetap terhubung?",
        "Mengapa nama pengguna saya tidak berubah di pengguna lain?",
        "Apakah riwayat pesan saya akan tersimpan?",
        "Jika pesan saya hilang, apa yang harus saya lakukan?",
        "Apa saja fitur di Obrol+",
        "Ke mana saya bisa melaporkan masalah lain?",
        "Siapa pengembang aplikasi Obrol+"
    )

    init {
        initializeConversation()
    }

    private fun initializeConversation() {
        viewModelScope.launch {
            val username = ownProfileRepository.getProfile().username.ifEmpty { "Pengguna" }
            val initialMessages = listOf(
                ServiceChatMessage.BotMessage(
                    text = "Halo, $username! Ada yang bisa saya bantu?",
                    isFirstMessage = true
                )
            )
            _uiState.value = ServiceChatUiState(
                conversation = initialMessages,
                username = username
            )
        }
    }

    fun onQuestionSelected(question: String) {
        val username = _uiState.value.username

        addMessage(ServiceChatMessage.UserMessage(question))
        setQuestionSheetVisibility(false)

        viewModelScope.launch {
            delay(3000)
            when (question) {
                "Bagaimana cara menambahkan kontak?" -> {
                    addMessage(ServiceChatMessage.BotImageMessage(R.raw.tutorial_koneksi, "Tutorial Koneksi"))
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Berikut adalah cara untuk menambahkan teman atau menghubungkan perangkat:\n\n" +
                                    "(Penting) pergi ke halaman Pengaturan pasang foto Profil dan juga Nama, setelah itu keluar dari Aplikasi dan hapus dari Bilah Navigasi\n\n" +
                                    "1. Buka aplikasi Obrol+.\n" +
                                    "2. Pastikan Wi-Fi di perangkatmu dan temanmu sudah aktif.\n" +
                                    "3. Pastikan juga GPS atau lokasimu sudah aktif.\n" +
                                    "4. Tunggu beberapa saat hingga perangkat terhubung secara otomatis.\n\n" +
                                    "Selamat mengObrol!"
                        )
                    )
                }
                "Bagaimana agar pesan tetap masuk ke saya?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Kamu dapat keluar dari aplikasi agar temanmu tetap dapat mengirim pesan, dengan syarat jangan mengeluarkan atau menghapus aplikasi dari jendela histori. Pastikan juga jarakmu tidak terlalu jauh dari temanmu."
                        )
                    )
                }
                "Berapa jarak Obrol+ supaya tetap terhubung?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Jarak paling umum untuk koneksi Obrol+ adalah sekitar 10-20 meter. Namun, kami sarankan untuk tidak terlalu jauh, apalagi jika ada tembok atau penghalang lain yang dapat memengaruhi sinyal."
                        )
                    )
                }
                "Mengapa nama pengguna saya tidak berubah di pengguna lain?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Hal ini biasanya terjadi karena perangkat tidak terhubung saat kamu mengubah nama. Pastikan kamu terhubung dengan temanmu sebelum atau sesaat setelah mengubah nama agar pembaruan dapat diterima oleh perangkat lain."
                        )
                    )
                }
                "Apakah riwayat pesan saya akan tersimpan?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Tentu saja. Semua riwayat pesanmu akan tersimpan dengan aman di penyimpanan lokal perangkatmu. Pesan akan tetap ada kecuali jika kamu menghapus data aplikasi atau menghapus aplikasi."
                        )
                    )
                }
                "Jika pesan saya hilang, apa yang harus saya lakukan?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username. Jika pesanmu hilang, kamu dapat mencoba keluar dari aplikasi, lalu menghapusnya dari daftar aplikasi yang baru dibuka. Setelah itu, coba buka kembali aplikasi. Seharusnya pesanmu akan muncul kembali."
                        )
                    )
                }
                "Apa saja fitur di Obrol+" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Halo, $username! Berikut adalah beberapa fitur yang bisa kamu coba di Obrol+:\n\n" +
                                    "1. Mengirim pesan teks, gambar, video, dan berkas lainnya.\n" +
                                    "2. Melakukan panggilan suara.\n" +
                                    "3. Mode gelap dan terang ( otomatis mengikuti sistem HP kamu).\n\n"

                        )
                    )
                }
                "Ke mana saya bisa melaporkan masalah lain?" -> {
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Jika kamu memiliki masalah lain, kamu dapat menghubungi pengembang, Raihaan Bagastiam Pratama, melalui Instagram atau WhatsApp."
                        )
                    )
                    addMessage(ServiceChatMessage.ActionButtons(listOf("Instagram", "WhatsApp")))
                }
                "Siapa pengembang aplikasi Obrol+" -> {
                    addMessage(ServiceChatMessage.BotImageMessage(R.raw.pengembang, "Foto Pengembang"))
                    addMessage(
                        ServiceChatMessage.BotMessage(
                            "Aplikasi Obrol+ dikembangkan oleh Raihaan Bagastiam Pratama. Seorang pengembang yang memiliki hobi dan ketertarikan mendalam di dunia teknologi, terutama dalam pengembangan aplikasi seluler."
                        )
                    )
                }
            }
        }
    }

    fun setQuestionSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(isQuestionSheetVisible = isVisible) }
    }

    fun clearConversation() {
        initializeConversation()
    }

    private fun addMessage(message: ServiceChatMessage) {
        _uiState.update {
            it.copy(conversation = it.conversation + message)
        }
    }
}
